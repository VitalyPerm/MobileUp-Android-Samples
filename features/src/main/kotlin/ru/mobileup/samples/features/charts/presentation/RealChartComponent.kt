package ru.mobileup.samples.features.charts.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.charts.createCartesianChartComponent
import ru.mobileup.samples.features.charts.createChartMenuComponent
import ru.mobileup.samples.features.charts.createRadialChartComponent
import ru.mobileup.samples.features.charts.domain.ChartMenu
import ru.mobileup.samples.features.charts.presentation.menu.ChartMenuComponent

class RealChartComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, ChartComponent {

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
    ): ChartComponent.Child = when (config) {
        ChildConfig.Menu -> ChartComponent.Child.Menu(
            componentFactory.createChartMenuComponent(componentContext, ::onChartMenuOutput)
        )

        ChildConfig.Cartesian -> ChartComponent.Child.Cartesian(
            componentFactory.createCartesianChartComponent(componentContext)
        )

        ChildConfig.Radial -> ChartComponent.Child.Radial(
            componentFactory.createRadialChartComponent(componentContext)
        )
    }

    private fun onChartMenuOutput(output: ChartMenuComponent.Output) {
        when (output) {
            is ChartMenuComponent.Output.ChartMenuRequest -> {
                when (output.chartMenu) {
                    ChartMenu.Cartesian -> navigation.safePush(ChildConfig.Cartesian)
                    ChartMenu.Radial -> navigation.safePush(ChildConfig.Radial)
                }
            }
        }
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object Menu : ChildConfig

        @Serializable
        data object Cartesian : ChildConfig

        @Serializable
        data object Radial : ChildConfig
    }
}