package ru.mobileup.samples.core.tutorial.domain

import dev.icerock.moko.resources.desc.StringDesc

data class Tutorial(
    val name: String,
    val messages: List<TutorialMessage>
)

data class TutorialMessage(
    val key: Any,
    val text: StringDesc
)