package ru.mobileup.samples.features.map.presentation.type

import ru.mobileup.samples.core.dialog.simple.SimpleDialogControl
import ru.mobileup.samples.core.map.domain.MapVendor

interface MapVendorComponent {

    val apiKeyNotFoundDialogControl: SimpleDialogControl<MapVendor>

    fun onVendorSelected(vendor: MapVendor)

    sealed interface Output {
        data class VendorSelected(val vendor: MapVendor) : Output
    }
}