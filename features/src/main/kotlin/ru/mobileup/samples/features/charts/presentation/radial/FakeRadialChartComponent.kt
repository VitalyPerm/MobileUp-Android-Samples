package ru.mobileup.samples.features.charts.presentation.radial

import ru.mobileup.samples.features.charts.data.ChartsData
import ru.mobileup.samples.features.charts.presentation.widgets.circleChart.CircleChartDataProducer

class FakeRadialChartComponent : RadialChartComponent {
    override val donutChartProducer =
        CircleChartDataProducer(ChartsData.CircleDiagramData.donutData())

    override val pieChartDataProducer =
        CircleChartDataProducer(ChartsData.CircleDiagramData.pieData())
}