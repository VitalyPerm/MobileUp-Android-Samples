package ru.mobileup.samples.features.document.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.predictiveBackAnimation
import ru.mobileup.samples.features.document.presentation.menu.DocumentMenuUi
import ru.mobileup.samples.features.document.presentation.preview.DocumentPreviewUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun DocumentUi(
    component: DocumentComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.collectAsState()

    Children(
        modifier = modifier,
        stack = childStack,
        animation = component.predictiveBackAnimation()
    ) { child ->
        when (val instance = child.instance) {
            is DocumentComponent.Child.Menu -> DocumentMenuUi(instance.component)
            is DocumentComponent.Child.Preview -> DocumentPreviewUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun DocumentUiPreview() {
    AppTheme {
        DocumentUi(FakeDocumentComponent())
    }
}