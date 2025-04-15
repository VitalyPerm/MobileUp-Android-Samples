package ru.mobileup.samples.features.map.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.utils.predictiveBackAnimation
import ru.mobileup.samples.features.map.presentation.MapUi
import ru.mobileup.samples.features.map.presentation.type.MapVendorUi

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MapMainUi(
    component: MapMainComponent,
    modifier: Modifier = Modifier
) {

    val childStack by component.childStack.collectAsState()

    Children(
        modifier = modifier,
        stack = childStack,
        animation = component.predictiveBackAnimation(),
    ) { child ->
        when (val instance = child.instance) {
            is MapMainComponent.Child.Map -> MapUi(instance.component)
            is MapMainComponent.Child.Vendor -> MapVendorUi(instance.component)
        }
    }
}
