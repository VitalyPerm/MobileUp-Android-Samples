package ru.mobileup.samples.core.tutorial.presentation.overlay

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

object TutorialHighlightedItems {
    val current = mutableStateMapOf<Any, HighlightableItem>()
}

data class HighlightableItem(
    val bounds: Rect,
    val shape: Shape
)

fun Modifier.highlightableItem(
    key: Any,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize(0, 0))
    }
    var position by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    val rect = remember(size, position) {
        Rect(
            size = size.toSize(),
            offset = position,
        )
    }

    DisposableEffect(Unit) {
        onDispose { TutorialHighlightedItems.current.remove(key) }
    }

    Modifier.onGloballyPositioned { coords ->
        size = coords.size
        position = coords.positionInWindow()
        TutorialHighlightedItems.current[key] = HighlightableItem(rect, shape)
    }
}
