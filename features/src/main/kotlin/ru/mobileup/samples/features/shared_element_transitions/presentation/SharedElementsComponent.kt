package ru.mobileup.samples.features.shared_element_transitions.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.shared_element_transitions.presentation.details.DetailsSharedElementsComponent
import ru.mobileup.samples.features.shared_element_transitions.presentation.list.ListSharedElementsComponent

interface SharedElementsComponent : BackHandlerOwner {
    val stack: StateFlow<ChildStack<*, Child>>

    fun onBackClick()

    sealed interface Child {
        class ListItems(val component: ListSharedElementsComponent) : Child
        class Details(val component: DetailsSharedElementsComponent) : Child
    }
}