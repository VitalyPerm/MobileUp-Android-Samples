package ru.mobileup.samples.features.divkit.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.PredictiveBackComponent
import ru.mobileup.samples.features.divkit.presentation.details.DivKitDetailsComponent
import ru.mobileup.samples.features.divkit.presentation.list.DivKitListComponent

interface DivKitComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class List(val component: DivKitListComponent) : Child
        class Details(val component: DivKitDetailsComponent) : Child
    }
}
