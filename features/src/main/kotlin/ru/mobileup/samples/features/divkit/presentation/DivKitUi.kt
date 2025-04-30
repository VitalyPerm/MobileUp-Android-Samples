package ru.mobileup.samples.features.divkit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.utils.predictiveBackAnimation
import ru.mobileup.samples.features.divkit.presentation.details.DivKitDetailsUi
import ru.mobileup.samples.features.divkit.presentation.list.DivKitListUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun DivKitUi(
    component: DivKitComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.collectAsState()

    Children(
        stack = childStack,
        animation = component.predictiveBackAnimation(),
        modifier = modifier
    ) { child ->
        when (val instance = child.instance) {
            is DivKitComponent.Child.Details -> DivKitDetailsUi(instance.component)

            is DivKitComponent.Child.List -> DivKitListUi(instance.component)
        }
    }
}