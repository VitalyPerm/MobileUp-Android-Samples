package ru.mobileup.samples.features.video.presentation.recorder.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.clickableNoRipple
import ru.mobileup.samples.features.R

private const val ANIMATION_DURATION_MS = 500

@Composable
fun CameraFlipIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    Icon(
        painter = painterResource(id = R.drawable.ic_camera_filp),
        contentDescription = "camera",
        tint = Color.Unspecified,
        modifier = modifier
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
private fun CameraFlipIconPreview() {
    AppTheme {
        Box {
            CameraFlipIcon(
                onClick = { }
            )
        }
    }
}