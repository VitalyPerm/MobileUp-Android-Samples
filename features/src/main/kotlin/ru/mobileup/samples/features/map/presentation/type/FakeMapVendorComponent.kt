package ru.mobileup.samples.features.map.presentation.type

import ru.mobileup.samples.core.dialog.simple.SimpleDialogControl
import ru.mobileup.samples.core.dialog.simple.fakeSimpleDialogControl
import ru.mobileup.samples.core.map.domain.MapVendor

class FakeMapVendorComponent : MapVendorComponent {
    override val apiKeyNotFoundDialogControl: SimpleDialogControl<MapVendor> =
        fakeSimpleDialogControl(MapVendor.Yandex)
    override fun onVendorSelected(vendor: MapVendor) = Unit
}