package ru.mobileup.samples.features.collapsing_toolbar.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.CollapsingToolbarMainComponent

interface CollapsingToolbarComponent : BackHandlerOwner {

    val stack: StateFlow<ChildStack<*, Child>>

    fun onBackClick()

    sealed interface Child {
        data class Main(val component: CollapsingToolbarMainComponent) : Child
        data object Common : Child
        data object Specific : Child
    }
}
