package ru.mobileup.samples.features.collapsing_toolbar.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.features.collapsing_toolbar.domain.ToolbarSample

@Composable
fun CollapsingToolbarMainUi(
    component: CollapsingToolbarMainComponent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ToolbarSample.entries.forEach {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                buttonType = ButtonType.Secondary,
                text = it.name,
                onClick = { component.onSampleClick(it) }
            )
        }
    }
}

@Preview
@Composable
private fun CollapsingToolbarMainPreview() {
    AppTheme {
        CollapsingToolbarMainUi(FakeCollapsingToolbarMainComponent())
    }
}
