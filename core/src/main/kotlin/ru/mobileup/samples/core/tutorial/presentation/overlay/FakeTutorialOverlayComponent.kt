package ru.mobileup.samples.core.tutorial.presentation.overlay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.tutorial.domain.TutorialMessage

class FakeTutorialOverlayComponent : TutorialOverlayComponent {

    override val visibleMessage: StateFlow<TutorialMessage?> = MutableStateFlow(null)

    override fun onOkClick() = Unit

    override fun onSkipClick() = Unit
}