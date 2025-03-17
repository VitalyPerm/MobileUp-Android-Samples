package ru.mobileup.samples.core.tutorial.presentation.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.icerock.moko.resources.compose.localized
import ru.mobileup.samples.core.R
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.tutorial.domain.TutorialMessage
import ru.mobileup.samples.core.utils.SystemBars
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import kotlin.math.roundToInt

@Composable
fun TutorialOverlayUi(
    component: TutorialOverlayComponent,
    modifier: Modifier = Modifier
) {
    val currentVisibleMessage by component.visibleMessage.collectAsState()

    val message = currentVisibleMessage ?: return
    val item = TutorialHighlightedItems.current[message.key] ?: return

    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()

    SystemBars(transparentNavigationBar = true)
    Box(modifier = modifier.fillMaxSize()) {
        Background(item)

        TutorialMessagePopup(
            message = message,
            onOkClick = component::onOkClick,
            highlightableItem = item,
        )

        AppButton(
            buttonType = ButtonType.Secondary,
            text = stringResource(id = R.string.tutorial_button_skip),
            onClick = component::onSkipClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp)
                .padding(navigationBarsPadding)
        )
    }
}

@Composable
private fun Background(
    item: HighlightableItem,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Canvas(
        modifier = modifier
            .fillMaxSize(),
    ) {
        val path = item.shape.createOutline(
            size = item.bounds.size,
            layoutDirection = layoutDirection,
            density = density
        ).let {
            when (it) {
                is Outline.Generic -> it.path
                is Outline.Rectangle -> Path().apply { addRect(it.rect) }
                is Outline.Rounded -> Path().apply { addRoundRect(it.roundRect) }
            }
        }.apply { translate(Offset(item.bounds.left, item.bounds.top)) }

        clipPath(
            path = path,
            clipOp = ClipOp.Difference,
        ) {
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                size = this.size,
            )
        }
    }
}

@Composable
private fun TutorialMessagePopup(
    message: TutorialMessage,
    onOkClick: () -> Unit,
    highlightableItem: HighlightableItem,
) {

    val properties = calculateTutorialMessagePopupProperties(highlightableItem.bounds)
    val offset = properties.offset.let {
        IntOffset(
            x = it.x.roundToInt(),
            y = it.y.roundToInt()
        )
    }

    val backgroundColor = CustomTheme.colors.background.screen

    Popup(
        offset = offset,
        properties = PopupProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column {
            Canvas(
                modifier = Modifier
                    .requiredSize(
                        width = 14.dp,
                        height = 12.dp
                    )
                    .offset { IntOffset(x = properties.pointerHorizontalOffset, y = 0) }
            ) {
                Path().apply {
                    moveTo(0f, size.height)
                    lineTo(size.width / 2, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }.let {
                    drawPath(
                        path = it,
                        color = backgroundColor,
                    )
                }
            }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier
                    .size(
                        width = TUTORIAL_MESSAGE_WIDTH_DP.dp,
                        height = TUTORIAL_MESSAGE_HEIGHT_DP.dp
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(
                            top = 24.dp,
                            bottom = 16.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message.text.localized(),
                        style = CustomTheme.typography.body.regular,
                        color = CustomTheme.colors.text.primary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onOkClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(id = R.string.tutorial_button_ok),
                            style = CustomTheme.typography.button.bold,
                            color = CustomTheme.colors.text.primary
                        )
                    }
                }
            }
        }
    }
}
