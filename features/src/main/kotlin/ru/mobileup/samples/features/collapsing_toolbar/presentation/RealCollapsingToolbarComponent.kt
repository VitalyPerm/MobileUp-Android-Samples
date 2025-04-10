package ru.mobileup.samples.features.collapsing_toolbar.presentation

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
import ru.mobileup.samples.features.collapsing_toolbar.createCollapsingToolbarMainComponent
import ru.mobileup.samples.features.collapsing_toolbar.domain.ToolbarSample
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.CollapsingToolbarMainComponent

class RealCollapsingToolbarComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, CollapsingToolbarComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: StateFlow<ChildStack<*, CollapsingToolbarComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Main,
        serializer = Config.serializer(),
        childFactory = ::createChild,
        handleBackButton = true
    ).toStateFlow(lifecycle)

    override fun onBackClick() = navigation.pop()

    private fun createChild(
        config: Config,
        componentContext: ComponentContext,
    ) = when (config) {
        Config.Main -> CollapsingToolbarComponent.Child.Main(
            componentFactory.createCollapsingToolbarMainComponent(componentContext, ::onMainOutput)
        )

        Config.Common -> CollapsingToolbarComponent.Child.Common
        Config.Specific -> CollapsingToolbarComponent.Child.Specific
    }

    private fun onMainOutput(output: CollapsingToolbarMainComponent.Output) = when (output) {
        is CollapsingToolbarMainComponent.Output.SampleChosen -> when (output.sample) {
            ToolbarSample.Common -> Config.Common
            ToolbarSample.Specific -> Config.Specific
        }.run(navigation::safePush)
    }

    @Serializable
    private sealed interface Config {

        @Serializable
        data object Main : Config

        @Serializable
        data object Common : Config

        @Serializable
        data object Specific : Config
    }
}
