package ru.mobileup.samples.features.collapsing_toolbar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.collapsing_toolbar.presentation.common.CollapsingToolbarCommonUi
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.CollapsingToolbarMainUi
import ru.mobileup.samples.features.collapsing_toolbar.presentation.specific.CollapsingToolbarSpecificUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CollapsingToolbarUi(
    component: CollapsingToolbarComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.collectAsState()

    Children(
        modifier = modifier,
        stack = stack,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + slide()),
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
            onBack = component::onBackClick,
        ),
    ) {
        when (val instance = it.instance) {
            is CollapsingToolbarComponent.Child.Main -> CollapsingToolbarMainUi(instance.component)
            CollapsingToolbarComponent.Child.Common -> CollapsingToolbarCommonUi()
            CollapsingToolbarComponent.Child.Specific -> CollapsingToolbarSpecificUi()
        }
    }
}

@Preview
@Composable
private fun CollapsingToolbarPreview() {
    AppTheme {
        CollapsingToolbarUi(FakeCollapsingToolbarComponent())
    }
}
