package ru.mobileup.samples.core.tutorial.presentation.management

import ru.mobileup.samples.core.tutorial.domain.TutorialManager
import ru.mobileup.samples.core.tutorial.domain.TutorialManagerImpl

class FakeTutorialManagementComponent : TutorialManagementComponent {
    override val tutorialManager: TutorialManager = TutorialManagerImpl()
}