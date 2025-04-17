package ru.mobileup.samples.features.map.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.PredictiveBackComponent
import ru.mobileup.samples.features.map.presentation.main.MapMainComponent
import ru.mobileup.samples.features.map.presentation.menu.MapMenuComponent

interface MapComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Map(val component: MapMainComponent) : Child
        class Menu(val component: MapMenuComponent) : Child
    }
}