package ru.mobileup.samples.features.map.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.map.domain.MapVendor
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.map.createMapComponent
import ru.mobileup.samples.features.map.createMapMenuComponent
import ru.mobileup.samples.features.map.presentation.menu.MapMenuComponent

class RealMapComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, MapComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Menu,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    override fun onBackClick() = navigation.pop()

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): MapComponent.Child = when (config) {
        is ChildConfig.Map -> MapComponent.Child.Map(
            componentFactory.createMapComponent(
                componentContext,
                config.vendor
            )
        )
        ChildConfig.Menu -> MapComponent.Child.Menu(
            componentFactory.createMapMenuComponent(
                componentContext,
                ::onMapMenuOutput
            )
        )
    }

    private fun onMapMenuOutput(output: MapMenuComponent.Output) {
        when (output) {
            is MapMenuComponent.Output.VendorSelected ->
                navigation.safePush(ChildConfig.Map(output.vendor))
        }
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data class Map(val vendor: MapVendor) : ChildConfig

        @Serializable
        data object Menu : ChildConfig
    }
}