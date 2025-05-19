package ru.mobileup.samples.features.ar.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandler
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.ar.presentation.menu.FakeARMenuComponent

class FakeARComponent : ARComponent {

    override val childStack: StateFlow<ChildStack<*, ARComponent.Child>> =
        createFakeChildStackStateFlow(
            ARComponent.Child.Menu(FakeARMenuComponent())
        )

    override val backHandler: BackHandler = fakeBackHandler

    override fun onBackClick() = Unit
}