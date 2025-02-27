package ru.mobileup.samples.features.charts.presentation.radial

import ru.mobileup.samples.features.charts.presentation.widgets.circleChart.CircleChartDataProducer

interface RadialChartComponent {

    val donutChartProducer: CircleChartDataProducer

    val pieChartDataProducer: CircleChartDataProducer
}