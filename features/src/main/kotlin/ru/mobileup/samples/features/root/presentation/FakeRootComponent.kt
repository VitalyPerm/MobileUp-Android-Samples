package ru.mobileup.samples.features.root.presentation

import ru.mobileup.samples.core.message.presentation.FakeMessageComponent
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.menu.presentation.FakeMenuComponent

class FakeRootComponent : RootComponent {

    override val childStack = createFakeChildStackStateFlow(
        RootComponent.Child.Menu(FakeMenuComponent())
    )

    override val messageComponent = FakeMessageComponent()
}
