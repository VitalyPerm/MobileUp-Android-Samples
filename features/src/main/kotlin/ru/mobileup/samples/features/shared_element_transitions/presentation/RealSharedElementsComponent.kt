package ru.mobileup.samples.features.shared_element_transitions.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.shared_element_transitions.createDetailsSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.createListSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement
import ru.mobileup.samples.features.shared_element_transitions.presentation.list.ListSharedElementsComponent

class RealSharedElementsComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, SharedElementsComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: StateFlow<ChildStack<*, SharedElementsComponent.Child>> = childStack(
            source = navigation,
            initialConfiguration = Config.ListItems,
            serializer = Config.serializer(),
            childFactory = ::createChild,
            handleBackButton = true
        ).toStateFlow(lifecycle)

    override fun onBackClick() = navigation.pop()

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ) = when (config) {
        is Config.Details -> SharedElementsComponent.Child.Details(
            componentFactory.createDetailsSharedElementsComponent(
                componentContext,
                config.item
            )
        )
        Config.ListItems -> SharedElementsComponent.Child.ListItems(
            componentFactory.createListSharedElementsComponent(
                componentContext,
                ::onListOutput
            )
        )
    }

    private fun onListOutput(output: ListSharedElementsComponent.Output) {
        when (output) {
            is ListSharedElementsComponent.Output.DetailsRequested -> navigation.safePush(Config.Details(output.item))
        }
    }

    @Serializable
    private sealed interface Config {

        @Serializable
        data object ListItems : Config

        @Serializable
        data class Details(val item: ItemSharedElement) : Config
    }
}