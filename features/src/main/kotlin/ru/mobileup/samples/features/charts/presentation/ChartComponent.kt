package ru.mobileup.samples.features.charts.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.PredictiveBackComponent
import ru.mobileup.samples.features.charts.presentation.cartesian.CartesianChartComponent
import ru.mobileup.samples.features.charts.presentation.menu.ChartMenuComponent
import ru.mobileup.samples.features.charts.presentation.radial.RadialChartComponent

interface ChartComponent : PredictiveBackComponent {
    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Menu(val component: ChartMenuComponent) : Child
        class Cartesian(val component: CartesianChartComponent) : Child
        class Radial(val component: RadialChartComponent) : Child
    }
}