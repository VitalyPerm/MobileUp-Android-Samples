package ru.mobileup.samples.features.document.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.document.presentation.menu.DocumentMenuComponent
import ru.mobileup.samples.features.document.presentation.preview.DocumentPreviewComponent

interface DocumentComponent : BackHandlerOwner {

    val childStack: StateFlow<ChildStack<*, Child>>

    fun onBackClick()

    sealed interface Child {
        class Menu(val component: DocumentMenuComponent) : Child
        class Preview(val component: DocumentPreviewComponent) : Child
    }
}