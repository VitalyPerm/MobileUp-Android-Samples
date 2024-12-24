package ru.mobileup.samples.features.menu

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.menu.presentation.MenuComponent
import ru.mobileup.samples.features.menu.presentation.RealMenuComponent

fun ComponentFactory.createMenuComponent(
    componentContext: ComponentContext,
    onOutput: (MenuComponent.Output) -> Unit
): MenuComponent {
    return RealMenuComponent(componentContext, onOutput)
}