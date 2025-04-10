package ru.mobileup.samples.features.video.presentation

import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.video.presentation.menu.FakeVideoMenuComponent

class FakeVideoComponent() : VideoComponent {

    override val childStack = createFakeChildStackStateFlow(
        VideoComponent.Child.Menu(FakeVideoMenuComponent())
    )

    override val backHandler = fakeBackHandler

    override fun onBackClick() = Unit
}
