package ru.mobileup.samples.features.map.presentation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.map.createMapComponent
import ru.mobileup.samples.features.map.createMapVendorComponent
import ru.mobileup.samples.core.map.domain.MapVendor
import ru.mobileup.samples.features.map.presentation.type.MapVendorComponent

class RealMapMainComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, MapMainComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Vendor,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override fun onBackClick() = navigation.pop()

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): MapMainComponent.Child = when (config) {
        is ChildConfig.Map -> MapMainComponent.Child.Map(
            componentFactory.createMapComponent(
                componentContext,
                config.vendor
            )
        )
        ChildConfig.Vendor -> MapMainComponent.Child.Vendor(
            componentFactory.createMapVendorComponent(
                componentContext,
                ::onMapVendorOutput
            )
        )
    }

    private fun onMapVendorOutput(output: MapVendorComponent.Output) {
        when (output) {
            is MapVendorComponent.Output.VendorSelected -> navigation.safePush(ChildConfig.Map(output.vendor))
        }
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data class Map(val vendor: MapVendor) : ChildConfig

        @Serializable
        data object Vendor : ChildConfig
    }
}