package ru.mobileup.samples.core.map.presentation.yandex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.samples.core.location.DEFAULT_MAP_ZOOM
import ru.mobileup.samples.core.location.GeoCameraPosition
import ru.mobileup.samples.core.location.GeoCoordinate
import ru.mobileup.samples.core.map.domain.MapCommand
import ru.mobileup.samples.core.map.domain.MapTheme
import ru.mobileup.samples.core.map.utils.executeCommand
import ru.mobileup.samples.core.map.utils.toGeoCoordinate
import ru.mobileup.samples.core.map.utils.toPoint
import ru.mobileup.samples.core.utils.LoadableState
import ru.mobileup.samples.core.utils.OnLifecycleEvent
import ru.mobileup.samples.core.utils.toPx
import kotlin.collections.orEmpty
import com.yandex.mapkit.logo.Alignment as LogoAlignment

@Composable
fun AppYandexMap(
    onPlaceClick: (GeoCoordinate) -> Unit,
    onClusterClick: (List<GeoCoordinate>) -> Unit,
    isCurrentLocationAvailable: Boolean,
    placesState: LoadableState<List<GeoCoordinate>>,
    modifier: Modifier = Modifier,
    initialCoordinate: GeoCoordinate = GeoCoordinate.KREMLIN,
    initialZoom: Float = DEFAULT_MAP_ZOOM,
    mapCommands: Flow<MapCommand> = emptyFlow(),
    theme: MapTheme = MapTheme.Default,
) {
    val myLocationOverlay = remember { MyLocationMarkerOverlay() }
    val placesPinOverlay = remember {
        YandexPlacePinsOverlay(
            onPlacePinClick = onPlaceClick,
            onClusterClick = onClusterClick
        )
    }

    LaunchedEffect(isCurrentLocationAvailable) {
        myLocationOverlay.updateIsCurrentLocationAvailable(isCurrentLocationAvailable)
    }

    LaunchedEffect(placesState) {
        placesPinOverlay.updatePlaces(placesState.data.orEmpty())
    }

    val overlays = listOf(myLocationOverlay, placesPinOverlay)

    val context = LocalContext.current
    var savedCameraPosition by rememberSaveable(stateSaver = GeoCameraPositionSaver) {
        mutableStateOf(
            GeoCameraPosition(
                geoCoordinate = initialCoordinate,
                zoom = initialZoom,
                azimuth = 0f,
                tilt = 0f
            )
        )
    }
    val mapView = remember { MapView(context) }.apply {
        mapWindow.map.addCameraListener { _, position, _, _ ->
            savedCameraPosition = GeoCameraPosition(
                geoCoordinate = position.target.toGeoCoordinate(),
                zoom = position.zoom,
                azimuth = position.azimuth,
                tilt = position.tilt
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mapCommands.collect(mapView::executeCommand)
        }
    }

    Box(modifier = modifier) {
        OnLifecycleEvent {
            when (it) {
                Lifecycle.Event.ON_START -> mapView.onStartMapKit()
                Lifecycle.Event.ON_STOP -> mapView.onStopMapKit()
                else -> Unit
            }
        }

        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val logoHorizontalPadding = 8.dp.toPx()
        val logoVerticalPadding = (statusBarPadding + 8.dp).toPx()

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.apply {
                    mapWindow.map.logo.setAlignment(
                        LogoAlignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
                    )
                    mapWindow.map.logo.setPadding(
                        Padding(logoHorizontalPadding, logoVerticalPadding)
                    )
                    mapWindow.map.move(
                        savedCameraPosition
                            .run {
                                CameraPosition(
                                    geoCoordinate.toPoint(),
                                    zoom,
                                    azimuth,
                                    tilt
                                )
                            }
                    )
                    overlays.forEach { overlay -> overlay.setup(this) }
                    onStartMapKit()
                }
            },
            update = {
                mapView.apply {
                    mapWindow.map.setMapStyle(theme.yandexJson)
                }
            }
        )
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
