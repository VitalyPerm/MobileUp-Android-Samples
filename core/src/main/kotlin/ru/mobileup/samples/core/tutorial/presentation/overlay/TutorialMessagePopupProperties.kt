package ru.mobileup.samples.core.tutorial.presentation.overlay

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.utils.toPx
import kotlin.math.abs
import kotlin.math.roundToInt

const val TUTORIAL_MESSAGE_HEIGHT_DP = 142f
const val TUTORIAL_MESSAGE_WIDTH_DP = 253f
const val TOP_PADDING_DP = 14f
const val POINTER_HORIZONTAL_PADDING_DP = 22f
const val POINTER_WIDTH_DP = 14f
const val POPUP_PADDING_DP = 4f

data class TutorialMessagePopupProperties(
    val pointerHorizontalOffset: Int,
    val offset: Offset
)

@Composable
fun calculateTutorialMessagePopupProperties(itemRect: Rect): TutorialMessagePopupProperties {

    val containerRect = LocalConfiguration.current.let {
        Rect(
            size = Size(
                width = it.screenWidthDp.dp.toPx().toFloat(),
                height = it.screenHeightDp.dp.toPx().toFloat()
            ),
            offset = Offset(0f, 0f)
        )
            .deflate(POPUP_PADDING_DP.dp.toPx().toFloat())
    }

    val width = TUTORIAL_MESSAGE_WIDTH_DP.dp
    val height = TUTORIAL_MESSAGE_HEIGHT_DP.dp
    val widthPx = width.toPx()
    val heightPx = height.toPx()

    val size = Size(widthPx.toFloat(), heightPx.toFloat())

    val topPadding = TOP_PADDING_DP.dp
    val topPaddingPx = topPadding.toPx()

    val pointerHorizontalPadding = POINTER_HORIZONTAL_PADDING_DP.dp
    val pointerHorizontalPaddingPx = pointerHorizontalPadding.toPx()

    val pointerWidth = POINTER_WIDTH_DP.dp
    val pointerWidthPx = pointerWidth.toPx()

    val pointerCenterOffsetPx = pointerHorizontalPaddingPx + pointerWidthPx / 2

    val rectWhenPointerOnStart = itemRect.bottomCenter.plus(
        Offset(
            x = -pointerCenterOffsetPx.toFloat(),
            y = +topPaddingPx.toFloat()
        )
    ).let {
        Rect(
            size = size,
            offset = it
        )
    }

    val rectWhenPointerInCenter = itemRect.bottomCenter.plus(
        Offset(
            x = -widthPx.toFloat() / 2,
            y = +topPaddingPx.toFloat()
        )
    ).let {
        Rect(
            size = size,
            offset = it
        )
    }

    val rectWhenPointerOnEnd = itemRect.bottomCenter.plus(
        Offset(
            x = -widthPx.toFloat() + pointerCenterOffsetPx,
            y = +topPaddingPx.toFloat()
        )
    ).let {
        Rect(
            size = size,
            offset = it
        )
    }

    val (pointerHorizontalOffset, offset) = when {
        rectWhenPointerOnStart in containerRect -> {
            pointerHorizontalPaddingPx to rectWhenPointerOnStart.topLeft
        }
        rectWhenPointerInCenter in containerRect -> {
            ((widthPx - pointerWidthPx) / 2) to rectWhenPointerInCenter.topLeft
        }
        rectWhenPointerOnEnd in containerRect -> {
            (widthPx - pointerHorizontalPaddingPx - pointerWidthPx) to rectWhenPointerOnEnd.topLeft
        }
        else -> {
            val translationDeltaToStart = containerRect.left - rectWhenPointerOnStart.left
            val translationDeltaToEnd = containerRect.right - rectWhenPointerOnEnd.right

            if (abs(translationDeltaToStart) < abs(translationDeltaToEnd)) {
                val pointerOffset = pointerHorizontalPaddingPx +
                        rectWhenPointerOnStart.left -
                        containerRect.left
                val offset = rectWhenPointerOnStart
                    .translate(
                        Offset(
                            x = containerRect.left - rectWhenPointerOnStart.left,
                            y = 0f
                        )
                    )
                    .topLeft
                pointerOffset.roundToInt() to offset
            } else {
                val pointerOffset = (widthPx - pointerHorizontalPaddingPx - pointerWidthPx) +
                        rectWhenPointerOnEnd.right -
                        containerRect.right

                val offset = rectWhenPointerOnEnd
                    .translate(
                        Offset(
                            x = containerRect.right - rectWhenPointerOnEnd.right,
                            y = 0f
                        )
                    )
                    .topLeft

                pointerOffset.roundToInt() to offset
            }
        }
    }

    return TutorialMessagePopupProperties(
        pointerHorizontalOffset = pointerHorizontalOffset,
        offset = offset
    )
}

private operator fun Rect.contains(other: Rect): Boolean {
    return this.left <= other.left && this.right >= other.right &&
            this.top <= other.top && this.bottom >= other.bottom
}
