package ru.mobileup.samples.features.charts.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.charts.presentation.cartesian.CartesianChartComponent
import ru.mobileup.samples.features.charts.presentation.menu.ChartMenuComponent
import ru.mobileup.samples.features.charts.presentation.radial.RadialChartComponent

interface ChartComponent : BackHandlerOwner {
    val childStack: StateFlow<ChildStack<*, Child>>

    fun onBackClick()

    sealed interface Child {
        class Menu(val component: ChartMenuComponent) : Child
        class Cartesian(val component: CartesianChartComponent) : Child
        class Radial(val component: RadialChartComponent) : Child
    }
}