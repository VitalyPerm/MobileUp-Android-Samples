package ru.mobileup.samples.features.charts.presentation.radial

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.charts.data.ChartsData
import ru.mobileup.samples.features.charts.presentation.widgets.circleChart.CircleChartDataProducer

class RealRadialChartComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, RadialChartComponent {

    override val donutChartProducer =
        CircleChartDataProducer(ChartsData.CircleDiagramData.donutData())

    override val pieChartDataProducer =
        CircleChartDataProducer(ChartsData.CircleDiagramData.pieData())

    init {
        componentScope.launch {
            while (true) { // Runtime update
                donutChartProducer.updateChartData(ChartsData.CircleDiagramData.donutData())
                pieChartDataProducer.updateChartData(ChartsData.CircleDiagramData.pieData())
                delay(5_000) // Waiting new data
            }
        }
    }
}