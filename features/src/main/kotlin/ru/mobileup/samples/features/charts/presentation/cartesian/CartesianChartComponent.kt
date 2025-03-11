package ru.mobileup.samples.features.charts.presentation.cartesian

import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer

interface CartesianChartComponent {
    val chartModelProducer: CartesianChartModelProducer
    val comboChartModelProducer: CartesianChartModelProducer
}