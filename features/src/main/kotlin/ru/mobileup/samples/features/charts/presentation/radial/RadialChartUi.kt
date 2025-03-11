package ru.mobileup.samples.features.charts.presentation.radial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.charts.presentation.widgets.circleChart.CircleChart
import ru.mobileup.samples.features.charts.presentation.widgets.circleChart.CircleChartType

@Composable
fun RadialChartUi(
    component: RadialChartComponent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(R.string.chart_pie))

        CircleChart(
            producer = component.donutChartProducer,
            chartType = CircleChartType.Donut,
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = stringResource(R.string.chart_donut))

        CircleChart(
            producer = component.pieChartDataProducer,
            chartType = CircleChartType.Pie,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRadialChartUi() {
    AppTheme {
        RadialChartUi(FakeRadialChartComponent())
    }
}