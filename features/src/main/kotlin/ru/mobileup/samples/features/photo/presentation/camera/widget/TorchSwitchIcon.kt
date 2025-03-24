package ru.mobileup.samples.features.photo.presentation.camera.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.clickableNoRipple
import ru.mobileup.samples.features.R

private const val ANIMATION_DURATION_MS = 500

@Composable
fun BoxScope.TorchSwitchIcon(
    torchState: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    Icon(
        painter = painterResource(
            id = if (torchState) {
                R.drawable.ic_torch_disabled
            } else {
                R.drawable.ic_torch
            }
        ),
        contentDescription = "camera",
        tint = Color.Unspecified,
        modifier = modifier
            .padding(end = 16.dp)
            .align(Alignment.CenterEnd)
            .graphicsLayer {
                this.rotationY = rotation.value * 360
            }
            .clickableNoRipple {
                onClick()
                scope.launch {
                    rotation.snapTo(0f)

                    rotation.animateTo(
                        1f,
                        animationSpec = tween(
                            durationMillis = ANIMATION_DURATION_MS,
                            easing = LinearEasing
                        )
                    )
                }
            }
    )
}

@Preview
@Composable
private fun TorchSwitchIconPreview() {
    AppTheme {
        Box {
            TorchSwitchIcon(
                torchState = true,
                onClick = { }
            )
        }
    }
}