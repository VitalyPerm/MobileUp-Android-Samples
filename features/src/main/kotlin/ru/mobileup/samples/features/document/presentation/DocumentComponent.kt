package ru.mobileup.samples.features.document.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.PredictiveBackComponent
import ru.mobileup.samples.features.document.presentation.menu.DocumentMenuComponent
import ru.mobileup.samples.features.document.presentation.preview.DocumentPreviewComponent

interface DocumentComponent : PredictiveBackComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        class Menu(val component: DocumentMenuComponent) : Child
        class Preview(val component: DocumentPreviewComponent) : Child
    }
}