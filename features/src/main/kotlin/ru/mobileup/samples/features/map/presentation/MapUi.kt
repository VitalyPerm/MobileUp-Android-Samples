package ru.mobileup.samples.features.map.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.predictiveBackAnimation
import ru.mobileup.samples.features.map.presentation.main.MapMainUi
import ru.mobileup.samples.features.map.presentation.menu.MapMenuUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MapUi(
    component: MapComponent,
    modifier: Modifier = Modifier
) {

    val childStack by component.childStack.collectAsState()

    Children(
        modifier = modifier,
        stack = childStack,
        animation = component.predictiveBackAnimation(),
    ) { child ->
        when (val instance = child.instance) {
            is MapComponent.Child.Map -> MapMainUi(instance.component)
            is MapComponent.Child.Menu -> MapMenuUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun MapPreview() {
    AppTheme {
        MapUi(FakeMapComponent())
    }
}
