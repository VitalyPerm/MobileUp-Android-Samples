package ru.mobileup.samples.features.charts.presentation.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.charts.domain.ChartMenu

@Composable
fun ChartMenuUi(
    component: ChartMenuComponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            buttonType = ButtonType.Secondary,
            text = stringResource(R.string.chart_menu_cartesian_charts),
            onClick = {
                component.onChartMenuClick(ChartMenu.Cartesian)
            }
        )

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            buttonType = ButtonType.Secondary,
            text = stringResource(R.string.chart_menu_radial_charts),
            onClick = {
                component.onChartMenuClick(ChartMenu.Radial)
            }
        )
    }
}