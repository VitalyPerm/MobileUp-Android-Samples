package ru.mobileup.samples.features.divkit.presentation.list

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.mobileup.samples.features.divkit.presentation.widgets.DivKitView

@Composable
fun DivKitListUi(
    component: DivKitListComponent,
    modifier: Modifier = Modifier
) {
    val content by component.content.collectAsState()

    DivKitView(content, modifier.safeDrawingPadding())
}
