package ru.mobileup.samples.features.photo.presentation.camera.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme

private const val BUTTON_SIZE_DP = 64

@Composable
fun CameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(BUTTON_SIZE_DP.dp)
            .clip(CircleShape)
            .clickable {
                onClick()
            }
    ) {
        drawCircle(
            brush = SolidColor(Color.White),
            style = Stroke(width = 8.dp.toPx())
        )

        drawCircle(
            radius = size.maxDimension / 2.5f,
            brush = SolidColor(Color.White),
        )
    }
}

@Preview
@Composable
private fun RecordingButtonPreview() {
    AppTheme {
        CameraButton(onClick = {})
    }
}