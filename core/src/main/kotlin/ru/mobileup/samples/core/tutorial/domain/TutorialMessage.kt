package ru.mobileup.samples.core.tutorial.domain

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import dev.icerock.moko.resources.desc.StringDesc

data class TutorialMessage(
    val key: Any,
    val text: StringDesc
)

data class HighlightableItem(
    val bounds: Rect,
    val shape: Shape
)