package ru.mobileup.samples.features.map.presentation.main

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.PredictiveBackComponent
import ru.mobileup.samples.features.map.presentation.MapComponent
import ru.mobileup.samples.features.map.presentation.type.MapVendorComponent

interface MapMainComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Map(val component: MapComponent) : Child
        class Vendor(val component: MapVendorComponent) : Child
    }
}