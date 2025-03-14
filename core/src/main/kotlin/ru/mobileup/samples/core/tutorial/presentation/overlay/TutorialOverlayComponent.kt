package ru.mobileup.samples.core.tutorial.presentation.overlay

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.tutorial.domain.TutorialMessage

interface TutorialOverlayComponent {

    val visibleMessage: StateFlow<TutorialMessage?>

    fun onOkClick()

    fun onSkipClick()
}