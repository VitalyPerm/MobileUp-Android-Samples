package ru.mobileup.samples.features.map.presentation.menu

import ru.mobileup.samples.core.dialog.simple.SimpleDialogControl
import ru.mobileup.samples.core.dialog.simple.fakeSimpleDialogControl
import ru.mobileup.samples.core.map.domain.MapVendor

class FakeMapMenuComponent : MapMenuComponent {
    override val apiKeyNotFoundDialogControl: SimpleDialogControl<MapVendor> =
        fakeSimpleDialogControl(MapVendor.Yandex)
    override fun onVendorSelected(vendor: MapVendor) = Unit
}