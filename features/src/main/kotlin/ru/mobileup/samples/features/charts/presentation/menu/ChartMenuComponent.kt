package ru.mobileup.samples.features.charts.presentation.menu

import ru.mobileup.samples.features.charts.domain.ChartMenu

interface ChartMenuComponent {

    fun onChartMenuClick(chartMenu: ChartMenu)

    sealed interface Output {
        data class ChartMenuRequest(val chartMenu: ChartMenu) : Output
    }
}