package ru.mobileup.samples.features.charts

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.charts.presentation.ChartComponent
import ru.mobileup.samples.features.charts.presentation.RealChartComponent
import ru.mobileup.samples.features.charts.presentation.cartesian.CartesianChartComponent
import ru.mobileup.samples.features.charts.presentation.cartesian.RealCartesianChartComponent
import ru.mobileup.samples.features.charts.presentation.menu.ChartMenuComponent
import ru.mobileup.samples.features.charts.presentation.menu.RealChartMenuComponent
import ru.mobileup.samples.features.charts.presentation.radial.RadialChartComponent
import ru.mobileup.samples.features.charts.presentation.radial.RealRadialChartComponent

fun ComponentFactory.createChartComponent(
    componentContext: ComponentContext,
): ChartComponent {
    return RealChartComponent(componentContext, get())
}

fun ComponentFactory.createChartMenuComponent(
    componentContext: ComponentContext,
    onOutput: (ChartMenuComponent.Output) -> Unit
): ChartMenuComponent {
    return RealChartMenuComponent(componentContext, onOutput)
}

fun ComponentFactory.createCartesianChartComponent(
    componentContext: ComponentContext
): CartesianChartComponent {
    return RealCartesianChartComponent(componentContext)
}

fun ComponentFactory.createRadialChartComponent(
    componentContext: ComponentContext
): RadialChartComponent {
    return RealRadialChartComponent(componentContext)
}