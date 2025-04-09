package ru.mobileup.samples.features.navigation.nested

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import ru.mobileup.samples.features.navigation.nested.leaf.NavigationNestedLeafUi
import ru.mobileup.samples.features.navigation.nested.main.NavigationNestedMainUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NavigationNestedUi(
    component: NavigationNestedComponent,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) {
    val stack by component.stack.collectAsState()

    Children(
        modifier = modifier.padding(paddingValues),
        stack = stack,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + slide()),
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
            onBack = component::onBackClick,
        ),
    ) {
        when (val instance = it.instance) {
            is NavigationNestedComponent.Child.LeafWithBottomBar -> NavigationNestedLeafUi(instance.component)
            is NavigationNestedComponent.Child.LeafWithoutBottomBar -> NavigationNestedLeafUi(
                instance.component
            )

            is NavigationNestedComponent.Child.Main -> NavigationNestedMainUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun NavigationNestedUiPreview() {
    AppTheme {
        NavigationNestedUi(FakeNavigationNestedComponent())
    }
}