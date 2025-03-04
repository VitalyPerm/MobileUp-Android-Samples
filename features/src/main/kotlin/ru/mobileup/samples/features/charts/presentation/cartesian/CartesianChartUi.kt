package ru.mobileup.samples.features.charts.presentation.cartesian

import android.graphics.Typeface
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberCandlestickCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.FadingEdges
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider.Companion.verticalGradient
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.charts.data.ChartsData.LegendLabelKey
import ru.mobileup.samples.features.charts.presentation.widgets.CandlestickRangeProvider
import ru.mobileup.samples.features.charts.presentation.widgets.markerBackground

private val lineColor1 = Color(0xFF673AB7)
private val lineColor2 = Color(0xFFEE6158)
private val lineColor3 = Color(0xFF84B650)
private val chartsHeight = 250.dp
private val comboChartsHeight = 320.dp

private const val chartsAnimationDuration = 1000

@Composable
fun CartesianChartUi(
    component: CartesianChartComponent,
    modifier: Modifier = Modifier
) {

    val startAxis = VerticalAxis.rememberStart(
        labelRotationDegrees = 15f
    )

    val bottomAxis = HorizontalAxis.rememberBottom(
        labelRotationDegrees = 15f
    )

    val marker = rememberDefaultCartesianMarker(
        label = TextComponent(
            typeface = Typeface.DEFAULT_BOLD,
            padding = Insets(verticalDp = 3f, horizontalDp = 5f),
            background = markerBackground(
                backgroundColor = Color.White,
                borderColor = Color.Black,
                round = 15f
            )
        ),
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        guideline = LineComponent(
            fill = fill(CustomTheme.colors.border.primary),
            thicknessDp = 1f
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.chart_line))

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.Line(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor1)),
                            pointConnector = LineCartesianLayer.PointConnector.cubic(0.2f),
                            areaFill = LineCartesianLayer.AreaFill.single(
                                fill(
                                    verticalGradient(
                                        lineColor1.toArgb(),
                                        Color.Transparent.toArgb()
                                    )
                                ),
                            )
                        ),
                    ),
                ),
                startAxis = startAxis,
                bottomAxis = bottomAxis,
                marker = marker,
            ),
            modelProducer = component.chartModelProducer,
            animationSpec = tween(durationMillis = chartsAnimationDuration),
            modifier = Modifier
                .fillMaxWidth()
                .height(chartsHeight),
            placeholder = { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        )

        Text(text = stringResource(R.string.chart_column))

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = fill(verticalGradient(lineColor2.toArgb(), lineColor1.toArgb())),
                            thickness = 20.dp,
                            shape = CorneredShape.rounded(
                                topLeftDp = 3f,
                                topRightDp = 3f
                            )
                        )
                    )
                ),
                startAxis = startAxis,
                bottomAxis = bottomAxis,
                marker = marker,
            ),
            modelProducer = component.chartModelProducer,
            animationSpec = tween(durationMillis = chartsAnimationDuration),
            modifier = Modifier
                .fillMaxWidth()
                .height(chartsHeight),
            placeholder = { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        )

        Text(text = stringResource(R.string.chart_candlestick))

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberCandlestickCartesianLayer(
                    rangeProvider = CandlestickRangeProvider
                ),
                startAxis = startAxis,
                bottomAxis = bottomAxis,
                marker = marker,
            ),
            modelProducer = component.chartModelProducer,
            animationSpec = tween(durationMillis = chartsAnimationDuration),
            modifier = Modifier
                .fillMaxWidth()
                .height(chartsHeight),
            placeholder = { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        )

        Text(text = stringResource(R.string.chart_combo))

        val comboChartsLineColors = remember {
            listOf(
                fill(verticalGradient(lineColor2.toArgb(), lineColor1.toArgb())),
                fill(lineColor1),
                fill(lineColor3)
            )
        }

        val legendItemLabelComponent = rememberTextComponent(CustomTheme.colors.border.primary)

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = comboChartsLineColors[0],
                            thickness = 20.dp,
                            shape = CorneredShape.rounded(
                                topLeftDp = 3f,
                                topRightDp = 3f
                            )
                        )
                    )
                ),
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.Line(
                            fill = LineCartesianLayer.LineFill.single(comboChartsLineColors[1]),
                            pointConnector = LineCartesianLayer.PointConnector.cubic(0.2f),
                            areaFill = LineCartesianLayer.AreaFill.single(
                                fill(
                                    verticalGradient(
                                        lineColor1.toArgb(),
                                        Color.Transparent.toArgb()
                                    )
                                ),
                            )
                        ),
                        LineCartesianLayer.Line(
                            fill = LineCartesianLayer.LineFill.single(comboChartsLineColors[2]),
                            pointConnector = LineCartesianLayer.PointConnector.cubic(0.2f),
                        ),
                    ),
                ),
                startAxis = startAxis,
                bottomAxis = bottomAxis,
                marker = marker,
                legend = rememberHorizontalLegend(
                    items = { extraStore ->
                        extraStore[LegendLabelKey].forEachIndexed { index, label ->
                            add(
                                LegendItem(
                                    shapeComponent(
                                        comboChartsLineColors[index],
                                        CorneredShape.Pill
                                    ),
                                    legendItemLabelComponent,
                                    label,
                                )
                            )
                        }
                    },
                    padding = insets(16.dp),
                ),
                fadingEdges = FadingEdges(widthDp = 16f)
            ),
            modelProducer = component.comboChartModelProducer,
            animationSpec = tween(durationMillis = chartsAnimationDuration),
            modifier = Modifier
                .fillMaxWidth()
                .height(comboChartsHeight),
            placeholder = { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCartesianChartUi() {
    AppTheme {
        CartesianChartUi(FakeCartesianChartComponent())
    }
}