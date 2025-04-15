package ru.mobileup.samples.features.map.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.dialog.BottomSheet
import ru.mobileup.samples.core.dialog.standard.StandardDialog
import ru.mobileup.samples.core.map.domain.MapVendor
import ru.mobileup.samples.core.map.presentation.AppGoogleMap
import ru.mobileup.samples.core.map.presentation.yandex.AppYandexMap
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.SystemBars
import ru.mobileup.samples.core.utils.clickableNoRipple
import ru.mobileup.samples.features.map.presentation.widgets.MapMyLocationButton
import ru.mobileup.samples.features.map.presentation.widgets.MapThemeSwitch
import ru.mobileup.samples.features.map.presentation.widgets.MapZoomButtons

@Composable
fun MapUi(
    component: MapComponent,
    modifier: Modifier = Modifier
) {
    val isCurrentLocationAvailable by component.isCurrentLocationAvailable.collectAsState()
    val isLocationSearchInProgress by component.isLocationSearchInProgress.collectAsState()
    val placesState by component.placesState.collectAsState()
    val userCoordinate by component.userCoordinate.collectAsState()
    val theme by component.theme.collectAsState()

    SystemBars(
        statusBarColor = Color.Transparent,
    )
    Box(
        modifier = modifier,
    ) {
        when (component.vendor) {
            MapVendor.Yandex -> AppYandexMap(
                mapCommands = component.mapCommands,
                theme = theme,
                onPlaceClick = component::onPlaceClick,
                onClusterClick = component::onClusterClick,
                isCurrentLocationAvailable = isCurrentLocationAvailable,
                placesState = placesState
            )
            MapVendor.Google -> AppGoogleMap(
                mapCommands = component.mapCommands,
                placesState = placesState,
                onPlaceClick = component::onPlaceClick,
                userCoordinate = userCoordinate,
                onClusterClick = component::onClusterClick,
                theme = theme
            )
        }

        MapZoomButtons(
            modifier = Modifier
                .align(BiasAlignment(0.9f, 0f)),
            theme = theme,
            onZoomInClick = component::onZoomInClick,
            onZoomOutClick = component::onZoomOutClick
        )
        MapMyLocationButton(
            modifier = Modifier
                .align(BiasAlignment(0.9f, 0.9f)),
            onClick = component::onMyLocationClick,
            isLocationSearchInProgress = isLocationSearchInProgress
        )

        MapThemeSwitch(
            theme = theme,
            onThemeSwitch = component::onThemeSwitch,
            modifier = Modifier
                .align(BiasAlignment(-0.9f, 0f)),
        )

        LoaderOverlay(placesState.loading)
    }

    BottomSheet(component.placeDialogControl) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Lat = ${it.lat}",
                modifier = Modifier
                    .padding(top = 16.dp)
            )
            Text(
                text = "Lng = ${it.lng}",
            )
        }
    }

    StandardDialog(dialogControl = component.locationDialogControl)
}

@Composable
private fun LoaderOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CustomTheme.colors.background.screen.copy(alpha = 0.7f))
                .clickableNoRipple {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}