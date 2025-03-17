package ru.mobileup.samples.features.collapsing_toolbar.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.CollapsingToolbarMainComponent

interface CollapsingToolbarComponent {

    val stack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        data class Main(val component: CollapsingToolbarMainComponent) : Child
        data object Common : Child
        data object Specific : Child
    }
}
