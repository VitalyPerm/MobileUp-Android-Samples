package ru.mobileup.samples.core.tutorial.data

import ru.mobileup.samples.core.settings.SettingsFactory
import ru.mobileup.samples.core.tutorial.domain.TutorialStatus

class TutorialStatusStorageImpl(
    settingsFactory: SettingsFactory
) : TutorialStatusStorage {

    companion object {
        private const val KEY_TUTORIAL_STATUS = "tutorial_status"
    }
    private val settings = settingsFactory.createSettings("tutorial_status")

    override suspend fun getStatus(tutorialName: String): TutorialStatus {
        return settings
            .getInt("${KEY_TUTORIAL_STATUS}_$tutorialName")
            .mapToTutorialStatus()
    }

    override suspend fun setStatus(tutorialName: String, status: TutorialStatus) {
        settings.putInt("${KEY_TUTORIAL_STATUS}_$tutorialName", status.mapToInt())
    }
}

private fun TutorialStatus.mapToInt() = when (this) {
    TutorialStatus.NotStarted -> -2
    TutorialStatus.Finished -> -1
    is TutorialStatus.InProgress -> messageIndex
}

private fun Int?.mapToTutorialStatus() = when (this) {
    null, -2 -> TutorialStatus.NotStarted
    -1 -> TutorialStatus.Finished
    else -> TutorialStatus.InProgress(this)
}