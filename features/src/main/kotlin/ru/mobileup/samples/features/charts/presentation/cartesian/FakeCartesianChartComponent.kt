package ru.mobileup.samples.features.charts.presentation.cartesian

import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mobileup.samples.features.charts.data.ChartsData.basicChartsData
import ru.mobileup.samples.features.charts.data.ChartsData.comboChartsData

class FakeCartesianChartComponent : CartesianChartComponent {
    override val chartModelProducer = CartesianChartModelProducer()
    override val comboChartModelProducer = CartesianChartModelProducer()

    init {
        GlobalScope.launch {
            chartModelProducer.runTransaction { basicChartsData() }
            comboChartModelProducer.runTransaction { comboChartsData() }
        }
    }
}