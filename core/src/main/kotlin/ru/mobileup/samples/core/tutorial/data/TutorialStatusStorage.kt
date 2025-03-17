package ru.mobileup.samples.core.tutorial.data

import ru.mobileup.samples.core.tutorial.domain.TutorialStatus

interface TutorialStatusStorage {
    suspend fun getStatus(tutorialName: String): TutorialStatus
    suspend fun setStatus(tutorialName: String, status: TutorialStatus)
}