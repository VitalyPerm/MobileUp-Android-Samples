package ru.mobileup.samples.features.charts.data

import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.candlestickSeries
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import ru.mobileup.samples.features.charts.presentation.widgets.circleChart.CircleChartData
import kotlin.random.Random
import kotlin.random.nextInt

object ChartsData {

    val LegendLabelKey = ExtraStore.Key<Set<String>>()

    fun CartesianChartModelProducer.Transaction.basicChartsData() {
        lineSeries { series(getRandomsNumbers()) }
        columnSeries { series(getRandomsNumbers()) }

        candlestickSeries(
            x = CandlestickData.x,
            opening = CandlestickData.opening,
            closing = CandlestickData.closing,
            low = CandlestickData.low,
            high = CandlestickData.high,
        )
    }

    fun CartesianChartModelProducer.Transaction.comboChartsData() {
        lineSeries {
            series(getRandomsNumbers())
            series(getRandomsNumbers())
        }
        columnSeries { series(getRandomsNumbers()) }
        extras { extraStore -> extraStore[LegendLabelKey] = setOf("Windows", "Linux", "MacOS") }
    }

    private fun getRandomsNumbers(count: Int = 15): List<Int> {
        return List(count) { Random.nextInt(0..50) }
    }

    private object CandlestickData {
        val x = (0..16).toList() + (18..23).toList()

        val opening = listOf<Number>(
                2634.899902,
                2635.300049,
                2630.899902,
                2628.800049,
                2623.600098,
                2624.600098,
                2623.100098,
                2629.399902,
                2635.100098,
                2618.100098,
                2623.699951,
                2613.699951,
                2612.199951,
                2618.699951,
                2619.100098,
                2620.300049,
                2621.800049,
                2620,
                2620.199951,
                2620.899902,
                2620.699951,
                2619.399902,
                2616.5,
            )

        val closing = listOf<Number>(
                2635.399902,
                2631.199951,
                2628.899902,
                2623.600098,
                2624.899902,
                2623.100098,
                2629.5,
                2635.100098,
                2618.300049,
                2623.699951,
                2613.600098,
                2612,
                2618.399902,
                2619,
                2620.300049,
                2621.899902,
                2620,
                2620.199951,
                2620.899902,
                2620.699951,
                2619.399902,
                2616.600098,
                2619.100098,
            )

        val low = listOf<Number>(
                2632,
                2630.199951,
                2627.600098,
                2621.5,
                2623.199951,
                2623.100098,
                2621.300049,
                2628.600098,
                2618,
                2616.800049,
                2611.899902,
                2608.399902,
                2612.199951,
                2616.300049,
                2616.5,
                2619.699951,
                2619.699951,
                2617.800049,
                2618.600098,
                2619.399902,
                2619.100098,
                2615.5,
                2616.300049,
            )

        val high = listOf<Number>(
                2636.5,
                2636.5,
                2631.899902,
                2629.600098,
                2629.699951,
                2626.899902,
                2631.699951,
                2636.199951,
                2636.899902,
                2626.800049,
                2623.899902,
                2615.699951,
                2618.899902,
                2619.699951,
                2621.699951,
                2623.199951,
                2622.100098,
                2620.899902,
                2621.800049,
                2624,
                2622.100098,
                2619.600098,
                2619.399902,
            )
    }

    object CircleDiagramData {
        fun pieData() =
            CircleChartData(
                slices = List(3) { Random.nextInt(10..40) }.mapIndexed { index, i ->
                    CircleChartData.Slice(
                        value = i.toFloat(),
                        label = "${index + 1} value"
                    )
                }
            )

        fun donutData() =
            CircleChartData(
                slices = List(6) { Random.nextInt(10..40) }.mapIndexed { index, i ->
                    CircleChartData.Slice(
                        value = i.toFloat(),
                        label = "${index + 1} value"
                    )
                }
            )
    }
}