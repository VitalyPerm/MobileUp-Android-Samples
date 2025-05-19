package ru.mobileup.samples.features.ar.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.PredictiveBackComponent
import ru.mobileup.samples.features.ar.presentation.menu.ARMenuComponent
import ru.mobileup.samples.features.ar.presentation.placement.ARPlacementComponent

interface ARComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Menu(val component: ARMenuComponent) : Child
        class Placement(val component: ARPlacementComponent) : Child
    }
}
