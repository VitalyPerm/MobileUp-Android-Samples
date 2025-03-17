package ru.mobileup.samples.core.tutorial.presentation.management

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mobileup.samples.core.error_handling.ErrorHandler
import ru.mobileup.samples.core.error_handling.safeLaunch
import ru.mobileup.samples.core.tutorial.data.TutorialStatusStorage
import ru.mobileup.samples.core.tutorial.domain.Tutorial
import ru.mobileup.samples.core.tutorial.domain.TutorialManager
import ru.mobileup.samples.core.tutorial.domain.TutorialStatus
import ru.mobileup.samples.core.utils.componentScope

class RealTutorialManagementComponent(
    componentContext: ComponentContext,
    private val tutorial: Tutorial,
    override val tutorialManager: TutorialManager,
    private val tutorialStatusStorage: TutorialStatusStorage,
    private val errorHandler: ErrorHandler
) : ComponentContext by componentContext, TutorialManagementComponent {

    private var currentIndex: Int = 0

    init {
        componentScope.safeLaunch(errorHandler) {
            when (val status = tutorialStatusStorage.getStatus(tutorial.name)) {
                TutorialStatus.Finished -> { /* do nothing */ }
                is TutorialStatus.InProgress -> {
                    currentIndex = status.messageIndex
                    tutorialManager.showMessage(tutorial.messages[currentIndex])
                }
                TutorialStatus.NotStarted -> {
                    currentIndex = 0
                    tutorialManager.showMessage(tutorial.messages[currentIndex])
                }
            }
        }

        tutorialManager
            .skipTutorialEventFlow
            .onEach {
                tutorialStatusStorage.setStatus(tutorial.name, TutorialStatus.Finished)
                tutorialManager.hideMessage()
            }
            .launchIn(componentScope)

        tutorialManager
            .proceedWithTutorialEventFlow
            .onEach { proceedWithTutorial() }
            .launchIn(componentScope)
    }

    private fun proceedWithTutorial() {
        currentIndex++
        componentScope.safeLaunch(errorHandler) {
            if (currentIndex < tutorial.messages.size) {
                tutorialStatusStorage.setStatus(tutorial.name, TutorialStatus.InProgress(currentIndex))
                tutorialManager.showMessage(tutorial.messages[currentIndex])
            } else {
                tutorialStatusStorage.setStatus(tutorial.name, TutorialStatus.Finished)
                tutorialManager.hideMessage()
            }
        }
    }
}