package ru.mobileup.samples.features.video.presentation.player.widgets

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.features.R

@Composable
fun RenderProgressIndicatorOverlay(
    progress: () -> Float,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CustomTheme.colors.palette.black50)
            .pointerInput(Unit) {}
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                progress = progress,
                color = CustomTheme.colors.palette.white,
                trackColor = Color.White.copy(0.7f)
            )

            val animatedProgress by animateIntAsState(
                targetValue = (progress() * 100).toInt()
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "${animatedProgress}%",
                color = CustomTheme.colors.text.invert
            )
        }

        OutlinedButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            onClick = onCancel
        ) {
            Text(
                text = stringResource(R.string.video_dismiss_btn),
                color = CustomTheme.colors.palette.white
            )
        }
    }
}

@Preview
@Composable
private fun RenderProgressIndicatorPreview() {
    AppTheme {
        RenderProgressIndicatorOverlay(
            progress = { 0.5f },
            onCancel = { }
        )
    }
}