package ru.mobileup.samples.features.charts.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
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
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + slide()),
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
            onBack = component::onBackClick,
        ),
    ) { child ->
        when (val instance = child.instance) {
            is ChartComponent.Child.Menu -> ChartMenuUi(instance.component)
            is ChartComponent.Child.Cartesian -> CartesianChartUi(instance.component)
            is ChartComponent.Child.Radial -> RadialChartUi(instance.component)
        }
    }
}