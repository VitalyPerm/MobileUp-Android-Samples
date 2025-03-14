package ru.mobileup.samples.core.tutorial.presentation.overlay

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.tutorial.domain.TutorialManager
import ru.mobileup.samples.core.tutorial.domain.TutorialMessage

class RealTutorialOverlayComponent(
    componentContext: ComponentContext,
    private val tutorialManager: TutorialManager
) : ComponentContext by componentContext, TutorialOverlayComponent {

    override val visibleMessage: StateFlow<TutorialMessage?> = tutorialManager.visibleMessage

    override fun onOkClick() {
        tutorialManager.proceedWithTutorial()
    }

    override fun onSkipClick() {
        tutorialManager.skipTutorial()
    }
}