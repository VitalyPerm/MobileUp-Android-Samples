package ru.mobileup.samples.features.collapsing_toolbar.presentation.specific.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import ru.mobileup.samples.core.utils.dispatchOnBackPressed
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.collapsing_toolbar.presentation.common.widget.CustomToolbarScrollBehavior

@Composable
fun SpecificToolbar(
    modifier: Modifier = Modifier,
    scrollBehavior: CustomToolbarScrollBehavior? = null,
) {
    val context = LocalContext.current

    val collapsedFraction by remember(scrollBehavior) {
        derivedStateOf {
            when (scrollBehavior) {
                null -> 1f
                else -> scrollBehavior.state.collapsedFraction
            }
        }
    }

    val shapePercentage by remember(collapsedFraction) {
        derivedStateOf {
            if (collapsedFraction < VisibilityThreshold) {
                0
            } else {
                lerp(0, 50, (collapsedFraction - VisibilityThreshold) * 5)
            }
        }
    }

    val colorAlpha by remember(collapsedFraction) {
        derivedStateOf {
            if (collapsedFraction < VisibilityThreshold) 0f else (collapsedFraction - VisibilityThreshold) * 5
        }
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Layout(
        modifier = Modifier
            .heightIn(min = MinCollapsedHeight)
            .then(modifier)
            .pointerInput(Unit) {}, // Needed to prevent the view from being clicked through, otherwise touches pass through the view
        content = {
            Spacer(
                modifier = Modifier
                    .layoutId(CollapsedBgId)
                    .drawWithCache {
                        val gradient = Brush.linearGradient(
                            colors = listOf(Color(0xFF021735), Color(0xFFEB0A33)),
                            start = Offset(0f, Float.POSITIVE_INFINITY),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                        onDrawBehind { drawRect(gradient) }
                    }
            )
            Image(
                modifier = Modifier.layoutId(ExpandedBgId),
                painter = painterResource(R.drawable.ic_football_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Image(
                modifier = Modifier
                    .layoutId(PlayerImageId)
                    .aspectRatio(1f)
                    .drawBehind {
                        drawRect(color = Color.White, alpha = colorAlpha)
                    }
                    .border(
                        width = 10.dp,
                        color = Color(0xFFF5F6F8).copy(alpha = colorAlpha),
                        shape = RoundedCornerShape(shapePercentage)
                    ),
                painter = painterResource(R.drawable.ic_football_player),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            IconButton(
                modifier = Modifier
                    .layoutId(NavigationIconId)
                    .padding(start = 8.dp, top = 8.dp + statusBarPadding)
                    .wrapContentHeight(Alignment.Top),
                onClick = { dispatchOnBackPressed(context) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = null
                )
            }

            Column(
                modifier = Modifier
                    .layoutId(PlayerNameId)
                    .graphicsLayer { alpha = colorAlpha }
            ) {
                Text(
                    text = "Robert",
                    fontWeight = FontWeight(500),
                    fontSize = 17.sp,
                    color = Color.White
                )
                Text(
                    text = "Lewandowski",
                    fontWeight = FontWeight(700),
                    fontSize = 23.sp,
                    color = Color.White
                )
            }

            Text(
                modifier = Modifier.layoutId(PlayerNumberId),
                text = "11",
                fontWeight = FontWeight(800),
                fontSize = 49.sp,
                color = Color.White
            )
        }
    ) { measurables, constraints ->
        val minCollapsedHeightPx = MinCollapsedHeight.toPx()
        val maxExpandedHeightPx = MaxExpandedHeight.toPx()
        val expandedImageHorizontalPaddingPx = ExpandedImageHorizontalPadding.toPx()
        val collapsedContentHorizontalPaddingPx = CollapsedContentHorizontalPadding.toPx()
        val collapsedImageBottomPaddingPx = CollapsedImageBottomPadding.toPx()
        val collapsedSpaceBetweenItemsPx = CollapsedSpaceBetweenItems.toPx()
        val minCollapsedImageSizePx = MinCollapsedImageSize.toPx()

        scrollBehavior?.state?.heightOffsetLimit = -(maxExpandedHeightPx - minCollapsedHeightPx)

        val layoutHeightPx =
            lerp(maxExpandedHeightPx, minCollapsedHeightPx, collapsedFraction).fastRoundToInt()

        val collapsedBgPlaceable = measurables
            .fastFirstOrNull { it.layoutId == CollapsedBgId }
            ?.measure(
                constraints.copy(
                    minWidth = constraints.maxWidth,
                    minHeight = layoutHeightPx,
                    maxHeight = layoutHeightPx,
                )
            )

        val expandedBgPlaceable = measurables
            .fastFirstOrNull { it.layoutId == ExpandedBgId }
            ?.measure(
                constraints.copy(
                    minWidth = constraints.maxWidth,
                    minHeight = layoutHeightPx,
                    maxHeight = layoutHeightPx
                )
            )

        val navIcon = measurables
            .fastFirstOrNull { it.layoutId == NavigationIconId }
            ?.measure(constraints.copy(minWidth = 0))

        val imagePlaceable = measurables
            .fastFirstOrNull { it.layoutId == PlayerImageId }
            ?.measure(
                constraints.copy(
                    maxWidth = constraints.maxWidth - expandedImageHorizontalPaddingPx.fastRoundToInt() * 2,
                    maxHeight = (maxExpandedHeightPx - statusBarPadding.toPx()).fastRoundToInt()
                )
            )

        val imageWidthPx = imagePlaceable?.width.orZero
        val imageHeightPx = imagePlaceable?.height.orZero

        val imageScaleStopX = minCollapsedImageSizePx / imageWidthPx
        val imageScaleStopY = minCollapsedImageSizePx / imageHeightPx
        val imageScaleX = lerp(1f, imageScaleStopX, collapsedFraction)
        val imageScaleY = lerp(1f, imageScaleStopY, collapsedFraction)

        val imageStartX = (constraints.maxWidth - imageWidthPx) / 2f
        val imageStopX = collapsedContentHorizontalPaddingPx
        val imageX = lerp(imageStartX, imageStopX, collapsedFraction).fastRoundToInt()

        val imageStartY = layoutHeightPx - imageHeightPx
        val imageStopY =
            (minCollapsedHeightPx - imageHeightPx - collapsedImageBottomPaddingPx).fastRoundToInt()
        val imageY = if (collapsedFraction < VisibilityThreshold) {
            imageStartY
        } else {
            val adjustedFraction = (collapsedFraction - VisibilityThreshold) / 0.2f
            lerp(imageStartY, imageStopY, adjustedFraction)
        }

        val numberPlaceable = measurables
            .fastFirstOrNull { it.layoutId == PlayerNumberId }
            ?.measure(
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0
                )
            )

        val numberHeightPx = numberPlaceable?.height.orZero
        val numberWidthPx = numberPlaceable?.width.orZero

        val numberX =
            (constraints.maxWidth - numberWidthPx - collapsedContentHorizontalPaddingPx).fastRoundToInt()

        val numberStartY = 0f
        val numberStopY =
            imageY + imageHeightPx - imageHeightPx * imageScaleY - (numberHeightPx - imageHeightPx * imageScaleY) * 0.5f
        val numberY = lerp(numberStartY, numberStopY, collapsedFraction).fastRoundToInt()

        val namePlaceable = measurables
            .fastFirstOrNull { it.layoutId == PlayerNameId }
            ?.measure(
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0,
                    maxWidth = (constraints.maxWidth - collapsedContentHorizontalPaddingPx * 2 - imageWidthPx * imageScaleStopX - collapsedSpaceBetweenItemsPx * 2 - numberWidthPx).fastRoundToInt(),
                )
            )

        val nameHeightPx = namePlaceable?.height.orZero

        val nameStartX = constraints.maxWidth.toFloat()
        val nameStopX = imageX + imageWidthPx * imageScaleX + collapsedSpaceBetweenItemsPx
        val nameX = lerp(nameStartX, nameStopX, collapsedFraction).fastRoundToInt()

        val nameStartY = layoutHeightPx - collapsedSpaceBetweenItemsPx
        val nameStopY =
            imageY + imageHeightPx - imageHeightPx * imageScaleY - (nameHeightPx - imageHeightPx * imageScaleY) * 0.5f
        val nameY = lerp(nameStartY, nameStopY, collapsedFraction).fastRoundToInt()

        layout(constraints.maxWidth, layoutHeightPx) {
            collapsedBgPlaceable?.placeRelativeWithLayer(x = 0, y = 0) {
                alpha = collapsedFraction
            }
            expandedBgPlaceable?.placeRelativeWithLayer(x = 0, y = 0) {
                alpha = 1 - collapsedFraction
            }
            navIcon?.placeRelative(x = 0, y = 0)
            imagePlaceable?.placeRelativeWithLayer(x = imageX, y = imageY) {
                clip = true
                shape = RoundedCornerShape(shapePercentage)
                scaleX = imageScaleX
                scaleY = imageScaleY
                transformOrigin = TransformOrigin(0f, 1f)
            }
            namePlaceable?.placeRelativeWithLayer(x = nameX, y = nameY) {
                alpha = colorAlpha
            }
            numberPlaceable?.placeRelativeWithLayer(x = numberX, y = numberY) {
                alpha = colorAlpha
            }
        }
    }
}

private val Int?.orZero: Int get() = this ?: 0

private val MinCollapsedHeight = 184.dp
private val MaxExpandedHeight = 366.dp
private val ExpandedImageHorizontalPadding = 32.dp
private val CollapsedContentHorizontalPadding = 20.dp
private val CollapsedImageBottomPadding = 16.dp
private val CollapsedSpaceBetweenItems = 16.dp
private val MinCollapsedImageSize = 40.dp

private const val VisibilityThreshold = 0.8f

private const val NavigationIconId = "navigationIcon"
private const val ExpandedBgId = "expandedBd"
private const val CollapsedBgId = "collapsedBd"
private const val PlayerImageId = "playerImage"
private const val PlayerNameId = "playerName"
private const val PlayerNumberId = "playerNumber"
