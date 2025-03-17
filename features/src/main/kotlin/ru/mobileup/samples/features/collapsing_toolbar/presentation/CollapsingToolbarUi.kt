package ru.mobileup.samples.features.collapsing_toolbar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.collapsing_toolbar.presentation.common.CollapsingToolbarCommonUi
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.CollapsingToolbarMainUi
import ru.mobileup.samples.features.collapsing_toolbar.presentation.specific.CollapsingToolbarSpecificUi

@Composable
fun CollapsingToolbarUi(
    component: CollapsingToolbarComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.collectAsState()

    Children(stack, modifier) {
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
