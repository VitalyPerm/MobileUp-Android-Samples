package ru.mobileup.samples.features.collapsing_toolbar.presentation

import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.FakeCollapsingToolbarMainComponent

class FakeCollapsingToolbarComponent : CollapsingToolbarComponent {

    override val stack = createFakeChildStackStateFlow(
        CollapsingToolbarComponent.Child.Main(FakeCollapsingToolbarMainComponent())
    )

    override val backHandler = fakeBackHandler

    override fun onBackClick() = Unit
}
