package ru.mobileup.samples.features.tutorial.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import ru.mobileup.samples.features.R

enum class TutorialMessageKeys {
    All,
    First,
    Second,
    Third,
    Back,
    Title;

    val message: StringDesc
        get() = when (this) {
            All -> R.string.tutorial_text_all
            First -> R.string.tutorial_text_first
            Second -> R.string.tutorial_text_second
            Third -> R.string.tutorial_text_third
            Back -> R.string.tutorial_text_back
            Title -> R.string.tutorial_text_title
        }.strResDesc()
}
