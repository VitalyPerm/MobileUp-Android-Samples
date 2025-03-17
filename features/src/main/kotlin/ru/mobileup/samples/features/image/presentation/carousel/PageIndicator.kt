package ru.mobileup.samples.features.image.presentation.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.custom.CustomTheme

@Composable
fun PageIndicator(
    imagesCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    defaultColor: Color = CustomTheme.colors.background.screen
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(imagesCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) defaultColor else defaultColor.copy(alpha = 0.6f)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }
}