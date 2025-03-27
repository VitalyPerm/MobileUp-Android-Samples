package ru.mobileup.samples.features.shared_element_transitions.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import ru.mobileup.samples.core.utils.SystemBars
import ru.mobileup.samples.core.widget.SharedChildStack
import ru.mobileup.samples.features.shared_element_transitions.presentation.details.DetailsSharedElementsUi
import ru.mobileup.samples.features.shared_element_transitions.presentation.list.ListSharedElementsUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun SharedElementsUi(
    component: SharedElementsComponent,
    modifier: Modifier = Modifier
) {
    SystemBars(transparentNavigationBar = true)

    val stack by component.stack.collectAsState()

    SharedChildStack(stack, modifier) {
        when (val instance = it.instance) {
            is SharedElementsComponent.Child.Details -> DetailsSharedElementsUi(instance.component)
            is SharedElementsComponent.Child.ListItems -> ListSharedElementsUi(instance.component)
        }
    }
}