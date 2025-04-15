package ru.mobileup.samples.core.map.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.samples.core.R
import ru.mobileup.samples.core.location.DEFAULT_MAP_ZOOM
import ru.mobileup.samples.core.location.GeoCoordinate
import ru.mobileup.samples.core.map.domain.MapCommand
import ru.mobileup.samples.core.map.domain.MapTheme
import ru.mobileup.samples.core.map.domain.toClusterItem
import ru.mobileup.samples.core.map.utils.executeCommand
import ru.mobileup.samples.core.map.utils.toLatLng
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.LoadableState

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun AppGoogleMap(
    placesState: LoadableState<List<GeoCoordinate>>,
    onPlaceClick: (GeoCoordinate) -> Unit,
    onClusterClick: (List<GeoCoordinate>) -> Unit,
    theme: MapTheme,
    modifier: Modifier = Modifier,
    mapCommands: Flow<MapCommand> = emptyFlow(),
    initialCoordinate: GeoCoordinate = GeoCoordinate.KREMLIN,
    initialZoom: Float = DEFAULT_MAP_ZOOM,
    userCoordinate: GeoCoordinate? = null
) {

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialCoordinate.toLatLng(), initialZoom
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            mapCommands.collect(cameraPositionState::executeCommand)
        }
    }

    val places = remember(placesState) {
        placesState.data?.map { it.toClusterItem() } ?: emptyList()
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = MapStyleOptions(theme.googleJson),
            isBuildingEnabled = true,
            isIndoorEnabled = true,
        ),
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
    ) {
        Clustering(
            items = places,
            onClusterItemClick = {
                onPlaceClick(it.itemPosition)
                true
            },
            onClusterClick = { cluster ->
                onClusterClick(cluster.items.map { it.itemPosition })
                true
            },
            clusterItemContent = { PointPin() },
            clusterContent = { ClusterPin(it.size) }
        )

        userCoordinate?.let {
            /**
             * Так же можно указать properties = MapProperties(isMyLocationEnabled: Boolean = true)
             * В этом случае карта сама нарисует стандартный пин текущего местоположения, так
             * же этот пин показывает точность местоположения в зависимости от силы сигнала
             */
            UserPositionPin(it)
        }
    }
}

@Composable
private fun ClusterPin(
    size: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(CustomTheme.colors.icon.warning, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = size.toString(),
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.title.regular
        )
    }
}

@Composable
private fun PointPin(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_map_pin),
        contentDescription = "Place",
        modifier = modifier
            .size(48.dp)
    )
}

@Composable
private fun UserPositionPin(
    coordinate: GeoCoordinate,
    modifier: Modifier = Modifier
) {
    MarkerComposable(
        state = rememberUpdatedMarkerState(coordinate.toLatLng()),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_pin_my_location),
            contentDescription = "Position",
            modifier = modifier
                .size(32.dp)
        )
    }
}
