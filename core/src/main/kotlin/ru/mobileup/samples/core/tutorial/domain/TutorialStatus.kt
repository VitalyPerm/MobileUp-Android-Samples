package ru.mobileup.samples.core.tutorial.domain

sealed interface TutorialStatus {
    data object NotStarted : TutorialStatus
    data class InProgress(val messageIndex: Int) : TutorialStatus
    data object Finished : TutorialStatus
}