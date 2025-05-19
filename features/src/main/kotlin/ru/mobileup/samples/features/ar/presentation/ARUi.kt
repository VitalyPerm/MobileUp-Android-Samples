package ru.mobileup.samples.features.ar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.utils.predictiveBackAnimation
import ru.mobileup.samples.features.ar.presentation.menu.ARMenuUi
import ru.mobileup.samples.features.ar.presentation.placement.ARPlacementUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ARUi(
    component: ARComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.collectAsState()

    Children(
        modifier = modifier,
        stack = childStack,
        animation = component.predictiveBackAnimation()
    ) { child ->
        when (val instance = child.instance) {
            is ARComponent.Child.Menu -> ARMenuUi(instance.component)
            is ARComponent.Child.Placement -> ARPlacementUi(instance.component)
        }
    }
}