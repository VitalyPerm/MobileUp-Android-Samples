package ru.mobileup.samples.features.charts.presentation.widgets.circleChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme

private val heightDonut = 250.dp
private const val donutWidth = 65f
private val partColor1 = Color(0xFF9575CD)
private val partColor2 = Color(0xFFE57373)
private val partColor3 = Color(0xFFC5E1A5)
private val partColor4 = Color(0xFF42A5F5)
private val partColor5 = Color(0xFFFF7043)
private val partColor6 = Color(0xFFFFEE58)
private val radialChartColors = listOf(
    partColor1, partColor2, partColor3,
    partColor4, partColor5, partColor6
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CircleChart(
    producer: CircleChartDataProducer,
    chartType: CircleChartType,
    modifier: Modifier = Modifier,
    startAngle: Float = -90f,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        val chartData by producer.data.collectAsState()

        Canvas(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(heightDonut)
                .aspectRatio(1f)
                .padding(16.dp)
        ) {
            var sAngle = startAngle

            val sweepAngles = chartData.sweepAngles()

            sweepAngles.forEachIndexed { index, progress ->
                drawArc(
                    color = radialChartColors[index % radialChartColors.size],
                    startAngle = sAngle,
                    sweepAngle = progress,
                    useCenter = when (chartType) {
                        CircleChartType.Pie -> true
                        CircleChartType.Donut -> false
                    },
                    size = size,
                    style = when (chartType) {
                        CircleChartType.Pie -> Fill
                        CircleChartType.Donut -> Stroke(
                            width = donutWidth,
                        )
                    },
                )
                sAngle += progress
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 3,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            chartData.slices.forEachIndexed { index, slice ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val color by remember(index) {
                        mutableStateOf(radialChartColors[index % radialChartColors.size])
                    }
                    Box(Modifier.size(16.dp).background(color))
                    Text(text = slice.label)
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDonutChart() {
    AppTheme {
        CircleChart(
            chartType = CircleChartType.Donut,
            producer = CircleChartDataProducer(
                CircleChartData(
                    slices = listOf(
                        CircleChartData.Slice(
                            value = 45f,
                            label = "Windows"
                        ),
                        CircleChartData.Slice(
                            value = 30f,
                            label = "Linux"
                        ),
                        CircleChartData.Slice(
                            value = 80f,
                            label = "MacOS"
                        ),
                    )
                )
            )
        )
    }
}