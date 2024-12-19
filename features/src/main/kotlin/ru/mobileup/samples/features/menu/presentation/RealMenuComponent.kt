package ru.mobileup.samples.features.menu.presentation

import com.arkivanov.decompose.ComponentContext

class RealMenuComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, MenuComponent {

    override fun onButtonClick() = Unit
}
