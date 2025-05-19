package ru.mobileup.samples.features.ar.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.ar.createArMenuComponent
import ru.mobileup.samples.features.ar.createArPlacementComponent
import ru.mobileup.samples.features.ar.presentation.menu.ARMenuComponent

class RealARComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ARComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.Placement,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext,
    ): ARComponent.Child = when (config) {
        ChildConfig.Menu -> ARComponent.Child.Menu(
            componentFactory.createArMenuComponent(componentContext, ::onMenuOutput)
        )
        ChildConfig.Placement -> ARComponent.Child.Placement(
            componentFactory.createArPlacementComponent(componentContext)
        )
    }

    private fun onMenuOutput(output: ARMenuComponent.Output) {
        when (output) {
            is ARMenuComponent.Output.PlacementRequested -> navigation.safePush(ChildConfig.Placement)
        }
    }

    override fun onBackClick() {
        navigation.pop()
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object Menu : ChildConfig

        @Serializable
        data object Placement : ChildConfig
    }
}