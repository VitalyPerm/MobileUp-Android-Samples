package ru.mobileup.samples.features.charts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.utils.predictiveBackAnimation
import ru.mobileup.samples.features.charts.presentation.cartesian.CartesianChartUi
import ru.mobileup.samples.features.charts.presentation.menu.ChartMenuUi
import ru.mobileup.samples.features.charts.presentation.radial.RadialChartUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ChartUi(
    component: ChartComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.collectAsState()

    Children(
        modifier = modifier,
        stack = childStack,
        animation = component.predictiveBackAnimation(),
    ) { child ->
        when (val instance = child.instance) {
            is ChartComponent.Child.Menu -> ChartMenuUi(instance.component)
            is ChartComponent.Child.Cartesian -> CartesianChartUi(instance.component)
            is ChartComponent.Child.Radial -> RadialChartUi(instance.component)
        }
    }
}