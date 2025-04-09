package ru.mobileup.samples.features.navigation.nested

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.navigation.nested.leaf.NavigationNestedLeafComponent
import ru.mobileup.samples.features.navigation.nested.main.NavigationNestedMainComponent

interface NavigationNestedComponent : BackHandlerOwner {

    val stack: StateFlow<ChildStack<*, Child>>

    val isBottomBarVisible: StateFlow<Boolean>

    fun onBackClick()

    sealed interface Child {
        class Main(val component: NavigationNestedMainComponent) : Child
        class LeafWithBottomBar(val component: NavigationNestedLeafComponent) : Child
        class LeafWithoutBottomBar(val component: NavigationNestedLeafComponent) : Child
    }
}