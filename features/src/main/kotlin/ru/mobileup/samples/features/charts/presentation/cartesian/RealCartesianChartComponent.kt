package ru.mobileup.samples.features.charts.presentation.cartesian

import com.arkivanov.decompose.ComponentContext
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.charts.data.ChartsData.basicChartsData
import ru.mobileup.samples.features.charts.data.ChartsData.comboChartsData

class RealCartesianChartComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, CartesianChartComponent {

    override val chartModelProducer = CartesianChartModelProducer()

    override val comboChartModelProducer = CartesianChartModelProducer()

    init {
        componentScope.launch {
            delay(1_500) // Loading
            while (true) { // Runtime update
                chartModelProducer.runTransaction { basicChartsData() }
                comboChartModelProducer.runTransaction { comboChartsData() }
                delay(5_000) // Waiting new data
            }
        }
    }
}