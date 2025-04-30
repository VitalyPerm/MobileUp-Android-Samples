package ru.mobileup.samples.features.divkit.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.divkit.createDivKitDetailsComponent
import ru.mobileup.samples.features.divkit.createDivKitListComponent
import ru.mobileup.samples.features.divkit.presentation.list.DivKitListComponent

class RealDivKitComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, DivKitComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = ChildConfig.List,
        serializer = ChildConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        config: ChildConfig,
        componentContext: ComponentContext
    ): DivKitComponent.Child = when (config) {
        is ChildConfig.Details -> DivKitComponent.Child.Details(
            componentFactory.createDivKitDetailsComponent(
                componentContext,
                config.title,
                config.jsonName
            )
        )
        ChildConfig.List -> DivKitComponent.Child.List(
            componentFactory.createDivKitListComponent(componentContext, ::onDivKitListOutput)
        )
    }

    private fun onDivKitListOutput(output: DivKitListComponent.Output) {
        when (output) {
            is DivKitListComponent.Output.DetailsRequested ->
                navigation.safePush(ChildConfig.Details(output.title, output.jsonName))
        }
    }

    override fun onBackClick() {
        navigation.pop()
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object List : ChildConfig

        @Serializable
        data class Details(
            val title: String,
            val jsonName: String
        ) : ChildConfig
    }
}