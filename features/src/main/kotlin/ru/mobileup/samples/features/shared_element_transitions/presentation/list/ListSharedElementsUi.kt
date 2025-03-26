package ru.mobileup.samples.features.shared_element_transitions.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.shared_element_transitions.presentation.widgets.ItemSharedElementUi

@Composable
fun ListSharedElementsUi(
    component: ListSharedElementsComponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        component.items.forEach { item ->
            ItemSharedElementUi(
                item = item,
                onClick = component::onClickItem,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewListSharedElementsUi() {
    AppTheme {
        ListSharedElementsUi(FakeListSharedElementsComponent())
    }
}