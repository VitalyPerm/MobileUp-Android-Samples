package ru.mobileup.samples.features.map.presentation.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import ru.mobileup.samples.core.dialog.simple.SimpleDialogControl
import ru.mobileup.samples.core.dialog.simple.fakeSimpleDialogControl
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.core.dialog.standard.fakeStandardDialogControl
import ru.mobileup.samples.core.location.GeoCoordinate
import ru.mobileup.samples.core.map.domain.MapCommand
import ru.mobileup.samples.core.map.domain.MapTheme
import ru.mobileup.samples.core.map.domain.MapVendor
import ru.mobileup.samples.core.utils.LoadableState

class FakeMapMainComponent : MapMainComponent {
    override val vendor: MapVendor = MapVendor.Yandex
    override val mapCommands: Flow<MapCommand> = emptyFlow()
    override val isCurrentLocationAvailable = MutableStateFlow(true)
    override val isLocationSearchInProgress = MutableStateFlow(true)
    override val userCoordinate = MutableStateFlow<GeoCoordinate?>(null)
    override val theme = MutableStateFlow(MapTheme.Default)
    override val placesState =
        MutableStateFlow(LoadableState<List<GeoCoordinate>>(data = emptyList()))
    override val placeDialogControl: SimpleDialogControl<GeoCoordinate> =
        fakeSimpleDialogControl(data = GeoCoordinate.Companion.KREMLIN)
    override val locationDialogControl: StandardDialogControl = fakeStandardDialogControl()

    override fun onMyLocationClick() = Unit
    override fun onZoomInClick() = Unit
    override fun onZoomOutClick() = Unit
    override fun onPlaceClick(place: GeoCoordinate) = Unit
    override fun onThemeSwitch(newTheme: MapTheme) = Unit
    override fun onRetryClick() = Unit
    override fun onClusterClick(places: List<GeoCoordinate>) = Unit
}