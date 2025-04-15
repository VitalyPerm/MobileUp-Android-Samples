package ru.mobileup.samples.features.map.presentation.type

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.core.BuildConfig
import ru.mobileup.samples.core.dialog.simple.SimpleDialogControl
import ru.mobileup.samples.core.dialog.simple.simpleDialogControl
import ru.mobileup.samples.core.map.domain.MapVendor

class RealMapVendorComponent(
    componentContext: ComponentContext,
    private val onOutput: (MapVendorComponent.Output) -> Unit
) : ComponentContext by componentContext, MapVendorComponent {

    override val apiKeyNotFoundDialogControl: SimpleDialogControl<MapVendor> = simpleDialogControl(
        key = "api_key_not_found",
    )

    override fun onVendorSelected(vendor: MapVendor) {
        when (vendor) {
            MapVendor.Yandex -> if (BuildConfig.YANDEX_MAP_API_KEY == "not_found") {
                apiKeyNotFoundDialogControl.show(vendor)
                return
            }
            MapVendor.Google -> if (BuildConfig.GOOGLE_MAP_API_KEY == "not_found") {
                apiKeyNotFoundDialogControl.show(vendor)
                return
            }
        }
        onOutput(MapVendorComponent.Output.VendorSelected(vendor))
    }
}