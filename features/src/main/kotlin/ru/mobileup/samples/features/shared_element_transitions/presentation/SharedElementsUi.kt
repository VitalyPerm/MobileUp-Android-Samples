package ru.mobileup.samples.features.shared_element_transitions.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.materialPredictiveBackAnimatable
import ru.mobileup.samples.core.widget.SharedChildStack
import ru.mobileup.samples.features.shared_element_transitions.presentation.details.DetailsSharedElementsUi
import ru.mobileup.samples.features.shared_element_transitions.presentation.list.ListSharedElementsUi

@OptIn(ExperimentalDecomposeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElementsUi(
    component: SharedElementsComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.collectAsState()

    SharedChildStack(
        modifier = modifier,
        stack = stack,
        animation = stackAnimation(
            predictiveBackParams = {
                PredictiveBackParams(
                    backHandler = component.backHandler,
                    onBack = component::onBackClick,
                    animatable = ::materialPredictiveBackAnimatable,
                )
            },
        ),
    ) {
        when (val instance = it.instance) {
            is SharedElementsComponent.Child.Details -> DetailsSharedElementsUi(instance.component)
            is SharedElementsComponent.Child.ListItems -> ListSharedElementsUi(instance.component)
        }
    }
}
