package ru.mobileup.samples.features.map.presentation

import com.arkivanov.essenty.backhandler.BackHandler
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.map.presentation.menu.FakeMapMenuComponent

class FakeMapComponent : MapComponent {
    override val childStack = createFakeChildStackStateFlow(
        MapComponent.Child.Menu(FakeMapMenuComponent())
    )

    override fun onBackClick() = Unit

    override val backHandler: BackHandler = fakeBackHandler
}