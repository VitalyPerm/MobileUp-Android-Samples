package ru.mobileup.samples.features.map.presentation.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.R
import ru.mobileup.samples.core.map.domain.MapTheme

@Composable
fun MapZoomButtons(
    theme: MapTheme,
    onZoomInClick: () -> Unit,
    onZoomOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        when (theme) {
            MapTheme.Dark -> Color(0xFF2C2C2C).copy(alpha = 0.8f)
            MapTheme.Default -> Color(0xFFEFEFEF).copy(alpha = 0.8f)
            MapTheme.Bright -> Color.White.copy(alpha = 0.8f)
        }
    )

    val borderColor by animateColorAsState(
        when (theme) {
            MapTheme.Dark -> Color(0xFF444444)
            else -> Color.White
        }
    )

    val iconTint by animateColorAsState(
        when (theme) {
            MapTheme.Dark -> Color.White
            else -> Color.Black
        }
    )

    val bgModifier = Modifier
        .clip(CircleShape)
        .size(48.dp)
        .drawBehind { drawRect(backgroundColor) }
        .border(1.dp, borderColor, CircleShape)
        .padding(8.dp)

    Column(
        modifier = modifier
    ) {
        IconButton(
            onClick = onZoomInClick,
            modifier = Modifier
        ) {
            Icon(
                modifier = bgModifier,
                painter = painterResource(R.drawable.ic_plus_16),
                tint = iconTint,
                contentDescription = null
            )
        }
        IconButton(
            onClick = onZoomOutClick
        ) {
            Icon(
                modifier = bgModifier,
                painter = painterResource(R.drawable.ic_minus_16),
                tint = iconTint,
                contentDescription = null
            )
        }
    }
}