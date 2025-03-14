package ru.mobileup.samples.core.tutorial.presentation.overlay

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import ru.mobileup.samples.core.tutorial.domain.HighlightableItem
import ru.mobileup.samples.core.tutorial.domain.TutorialManager

object TutorialHighlightedItem {
    var current by mutableStateOf<HighlightableItem?>(null)
        internal set
}

fun Modifier.highlightableItem(
    key: Any,
    tutorialManager: TutorialManager,
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

    val currentVisibleMessage by tutorialManager.visibleMessage.collectAsState()
    val visibleMessageKey = currentVisibleMessage?.key

    LaunchedEffect(key, visibleMessageKey, rect) {
        if (key == visibleMessageKey) {
            TutorialHighlightedItem.current = HighlightableItem(rect, shape)
        }
    }

    DisposableEffect(key, visibleMessageKey) {
        onDispose {
            if (key != visibleMessageKey) {
                TutorialHighlightedItem.current = null
            }
        }
    }

    Modifier.onGloballyPositioned { coords ->
        size = coords.size
        position = coords.positionInWindow()
    }
}
