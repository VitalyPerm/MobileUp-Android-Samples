package ru.mobileup.samples.features.charts.presentation.menu

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.features.charts.domain.ChartMenu

class RealChartMenuComponent(
    componentContext: ComponentContext,
    private val onOutput: (ChartMenuComponent.Output) -> Unit
) : ComponentContext by componentContext, ChartMenuComponent {

    override fun onChartMenuClick(chartMenu: ChartMenu) {
        onOutput(ChartMenuComponent.Output.ChartMenuRequest(chartMenu))
    }
}