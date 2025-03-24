package ru.mobileup.samples.features.yandex_map.presentation.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.samples.core.location.DEFAULT_MAP_ZOOM
import ru.mobileup.samples.core.location.GeoCameraPosition
import ru.mobileup.samples.core.location.GeoCameraPositionSaver
import ru.mobileup.samples.core.location.GeoCoordinate
import ru.mobileup.samples.core.utils.OnLifecycleEvent
import ru.mobileup.samples.core.utils.navigationBarsPaddingDp
import ru.mobileup.samples.features.yandex_map.domain.MapCommand
import ru.mobileup.samples.features.yandex_map.domain.YandexMapRectangleCoordinate
import ru.mobileup.samples.features.yandex_map.domain.YandexMapTheme
import ru.mobileup.samples.features.yandex_map.domain.executeCommand
import ru.mobileup.samples.features.yandex_map.utils.toPoint
import com.yandex.mapkit.logo.Alignment as LogoAlignment

private const val MAP_DARK_STYLE = """
    [
        {
            "types": ["polyline", "polygon", "point"],
            "tags": {"none": []},
            "stylers": {"saturation": -1}
        }
    ]
"""

private const val MAP_DEFAULT_STYLE = """
    [
        {
            "types": ["polyline", "polygon", "point"],
            "tags": {"none": []},
            "stylers": {"saturation": 0}
        }
    ]
"""

private const val MAP_BRIGHT_STYLE = """
    [
        {
            "types": ["polyline", "polygon", "point"],
            "tags": {"none": []},
            "stylers": {"saturation": 1}
        }
    ]
"""

@Composable
fun YandexMapViewUi(
    modifier: Modifier = Modifier,
    initialCoordinate: GeoCoordinate = GeoCoordinate.KREMLIN,
    initialZoom: Float = DEFAULT_MAP_ZOOM,
    mapCommands: Flow<MapCommand> = emptyFlow(),
    frameCoordinateChanged: (YandexMapRectangleCoordinate) -> Unit = { },
    cameraPositionChanged: (GeoCoordinate) -> Unit = {},
    onMapMovingChanged: (Boolean) -> Unit = {},
    onMapTap: () -> Unit = {},
    theme: YandexMapTheme,
    overlays: List<MapOverlay> = emptyList(),
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val savedCameraPosition = rememberSaveable(saver = GeoCameraPositionSaver) {
        GeoCameraPosition(
            geoCoordinate = initialCoordinate,
            zoom = initialZoom,
            azimuth = 0f,
            tilt = 0f
        )
    }

    val cameraListener by remember {
        mutableStateOf(
            CameraListener { _, camPos, _, isNotMoving ->
                onMapMovingChanged(!isNotMoving)
                if (isNotMoving) {
                    val visibleRegion = mapView.mapWindow.map.visibleRegion

                    frameCoordinateChanged(
                        YandexMapRectangleCoordinate(
                            topRight = GeoCoordinate(
                                lat = visibleRegion.topRight.latitude,
                                lng = visibleRegion.topRight.longitude,
                            ),
                            topLeft = GeoCoordinate(
                                lat = visibleRegion.topLeft.latitude,
                                lng = visibleRegion.topLeft.longitude,
                            ),
                            bottomRight = GeoCoordinate(
                                lat = visibleRegion.bottomRight.latitude,
                                lng = visibleRegion.bottomRight.longitude,
                            ),
                            bottomLeft = GeoCoordinate(
                                lat = visibleRegion.bottomLeft.latitude,
                                lng = visibleRegion.bottomLeft.longitude,
                            ),
                            zoom = camPos.zoom
                        )
                    )

                    cameraPositionChanged(
                        GeoCoordinate(camPos.target.latitude, camPos.target.longitude)
                    )
                }
            }
        )
    }

    val inputListener by remember {
        mutableStateOf(
            object : InputListener {
                override fun onMapLongTap(map: Map, point: Point) = Unit

                override fun onMapTap(map: Map, point: Point) {
                    onMapTap()
                }
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mapCommands.collect(mapView::executeCommand)
        }
    }

    Box(modifier = modifier) {
        if (!LocalInspectionMode.current) {
            OnLifecycleEvent {
                when (it) {
                    Lifecycle.Event.ON_START -> mapView.onStartMapKit()
                    Lifecycle.Event.ON_STOP -> mapView.onStopMapKit()
                    else -> {}
                }
            }
            val navigationBarsPadding = navigationBarsPaddingDp()
            AndroidView(
                modifier = modifier.fillMaxSize(),
                factory = {
                    mapView.apply {
                        mapWindow.map.logo.setAlignment(
                            LogoAlignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
                        )

                        val density = context.resources.displayMetrics.density
                        mapWindow.map.logo.setPadding(
                            Padding((8 * density).toInt(), ((navigationBarsPadding.value + 8) * density).toInt())
                        )
                        mapWindow.map.move(
                            savedCameraPosition
                                .run { CameraPosition(geoCoordinate.toPoint(), zoom, azimuth, tilt) }
                        )
                        mapWindow.map.addCameraListener(cameraListener)
                        mapWindow.map.addInputListener(inputListener)
                        overlays.forEach { overlay -> overlay.setup(this) }
                        mapView.onStartMapKit()
                    }
                },
                update = {
                    mapView.apply {
                        mapWindow.map.setMapStyle(
                            when (theme) {
                                YandexMapTheme.Bright -> MAP_BRIGHT_STYLE
                                YandexMapTheme.Default -> MAP_DEFAULT_STYLE
                                YandexMapTheme.Dark -> MAP_DARK_STYLE
                            }
                        )
                    }
                }
            )
        }
    }
}

private fun MapView?.onStartMapKit() {
    MapKitFactory.getInstance().onStart()
    this?.onStart()
}

private fun MapView?.onStopMapKit() {
    MapKitFactory.getInstance().onStop()
    this?.onStop()
}
