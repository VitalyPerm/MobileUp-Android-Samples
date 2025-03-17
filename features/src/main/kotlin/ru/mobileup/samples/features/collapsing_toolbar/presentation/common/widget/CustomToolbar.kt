package ru.mobileup.samples.features.collapsing_toolbar.presentation.common.widget

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import kotlin.math.max

object CustomToolbarDefaults {

    val containerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.background

    val windowInsets: WindowInsets
        @Composable
        get() = WindowInsets.systemBars.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        )

    val collapsedElevation = 4.dp

    @Composable
    fun customToolbarScrollBehavior(
        state: CustomToolbarState = rememberCustomToolbarState(),
        snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
        flingAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    ) = CustomToolbarScrollBehavior(state, snapAnimationSpec, flingAnimationSpec)
}

@Immutable
data class CollapsingTitle(
    val text: String,
    val expandedTextStyle: TextStyle,
    val maxExpandedLines: Int = 3,
)

@Immutable
data class CollapsingToolbarPaddingValues(
    val navIconStartPadding: Dp = 4.dp,
    val actionsEndPadding: Dp = 4.dp,
    val titleStartPadding: Dp = 16.dp,
    val titleEndPadding: Dp = 16.dp,
)

@Composable
fun CustomToolbar(
    modifier: Modifier = Modifier,
    collapsingTitle: CollapsingTitle? = null,
    navigationIcon: (@Composable BoxScope.() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    topContent: (@Composable BoxScope.() -> Unit)? = null,
    bottomContent: (@Composable BoxScope.() -> Unit)? = null,
    containerColor: Color = CustomToolbarDefaults.containerColor,
    collapsedElevation: Dp = CustomToolbarDefaults.collapsedElevation,
    windowInsets: WindowInsets = CustomToolbarDefaults.windowInsets,
    contentPadding: CollapsingToolbarPaddingValues = CollapsingToolbarPaddingValues(),
    scrollBehavior: CustomToolbarScrollBehavior? = null,
) {
    val collapsedFraction by remember(scrollBehavior, topContent) {
        derivedStateOf {
            when {
                scrollBehavior != null && topContent == null -> scrollBehavior.state.collapsedFraction
                scrollBehavior != null && topContent != null -> 0f
                else -> 1f
            }
        }
    }

    val fullyCollapsedTitleScale = remember(collapsingTitle) {
        when (collapsingTitle) {
            null -> 1f
            else -> CollapsedTitleLineHeight.value / collapsingTitle.expandedTextStyle.lineHeight.value
        }
    }

    val collapsingTitleScale by remember(collapsedFraction, fullyCollapsedTitleScale) {
        derivedStateOf {
            lerp(1f, fullyCollapsedTitleScale, collapsedFraction)
        }
    }

    val showElevation by remember(scrollBehavior, collapsedFraction) {
        derivedStateOf {
            when {
                scrollBehavior == null -> false
                scrollBehavior.state.contentOffset < 0f && collapsedFraction == 1f -> true
                scrollBehavior.state.contentOffset < -1f && topContent != null -> true
                else -> false
            }
        }
    }

    val pivotPoint = remember { TransformOrigin(0f, 0f) }

    val elevation by animateDpAsState(if (showElevation) collapsedElevation else 0.dp)

    Layout(
        modifier = Modifier
            .graphicsLayer {
                shadowElevation = elevation.toPx()
            }
            .heightIn(min = MinCollapsedHeight)
            .background(containerColor)
            .windowInsetsPadding(windowInsets)
            .then(modifier)
            .pointerInput(Unit) {}, // Needed to prevent the view from being clicked through, otherwise touches pass through the view
        content = {
            if (collapsingTitle != null) {
                Text(
                    modifier = Modifier
                        .layoutId(ExpandedTitleId)
                        .wrapContentHeight(align = Alignment.Top),
                    text = collapsingTitle.text,
                    style = collapsingTitle.expandedTextStyle,
                    maxLines = collapsingTitle.maxExpandedLines,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .layoutId(CollapsedTitleId)
                        .wrapContentHeight(align = Alignment.Top),
                    text = collapsingTitle.text,
                    style = collapsingTitle.expandedTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (navigationIcon != null) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .layoutId(NavigationIconId),
                    content = navigationIcon
                )
            }

            if (actions != null) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .layoutId(ActionsId),
                    content = actions
                )
            }

            if (topContent != null) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .layoutId(TopContentId),
                    content = topContent
                )
            }

            if (bottomContent != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId(BottomContentId),
                    content = bottomContent
                )
            }
        },
    ) { measurables, constraints ->
        val expandedTitleBottomPaddingPx = ExpandedTitleBottomPadding.toPx()

        val navIconStartPaddingPx = contentPadding.navIconStartPadding.toPx()
        val actionsEndPaddingPx = contentPadding.actionsEndPadding.toPx()

        val titleStartPaddingPx = contentPadding.titleStartPadding.toPx()
        val titleEndPaddingPx = contentPadding.titleEndPadding.toPx()

        // Measuring widgets inside toolbar:

        val navigationIconPlaceable = measurables
            .fastFirstOrNull { it.layoutId == NavigationIconId }
            ?.measure(constraints.copy(minWidth = 0))

        val actionsPlaceable = measurables
            .fastFirstOrNull { it.layoutId == ActionsId }
            ?.measure(constraints.copy(minWidth = 0))

        val expandedTitlePlaceable = measurables
            .fastFirstOrNull { it.layoutId == ExpandedTitleId }
            ?.measure(
                constraints.copy(
                    maxWidth = (constraints.maxWidth - titleStartPaddingPx - titleEndPaddingPx).fastRoundToInt(),
                    minWidth = 0,
                    minHeight = 0
                )
            )

        val bottomContentPlaceable = measurables
            .fastFirstOrNull { it.layoutId == BottomContentId }
            ?.measure(constraints)

        val navigationIconOffset = when (navigationIconPlaceable) {
            null -> titleStartPaddingPx
            else -> navigationIconPlaceable.width + navIconStartPaddingPx * 2
        }

        val actionsOffset = when (actionsPlaceable) {
            null -> titleEndPaddingPx
            else -> actionsPlaceable.width + actionsEndPaddingPx * 2
        }

        val collapsedTitleMaxWidthPx =
            (constraints.maxWidth - navigationIconOffset - actionsOffset) / fullyCollapsedTitleScale

        val collapsedTitlePlaceable = measurables
            .fastFirstOrNull { it.layoutId == CollapsedTitleId }
            ?.measure(
                constraints.copy(
                    maxWidth = collapsedTitleMaxWidthPx.fastRoundToInt(),
                    minWidth = 0,
                    minHeight = 0
                )
            )

        val topContentPlaceable = measurables
            .fastFirstOrNull { it.layoutId == TopContentId }
            ?.measure(
                constraints.copy(
                    minWidth = 0,
                    maxWidth = (constraints.maxWidth - navigationIconOffset - actionsOffset).fastRoundToInt()
                )
            )

        val collapsedHeightPx = when (topContentPlaceable) {
            null -> MinCollapsedHeight.toPx()
            else -> max(
                MinCollapsedHeight.toPx(),
                topContentPlaceable.height.toFloat()
            )
        }

        var layoutHeightPx = collapsedHeightPx.fastRoundToInt()

        // Calculating coordinates of widgets inside toolbar:

        // Current coordinates of navigation icon
        val navigationIconX = navIconStartPaddingPx.fastRoundToInt()
        val navigationIconY =
            ((collapsedHeightPx - navigationIconPlaceable?.height.orZero) / 2).fastRoundToInt()

        // Current coordinates of actions
        val actionsX =
            (constraints.maxWidth - actionsPlaceable?.width.orZero - actionsEndPaddingPx).fastRoundToInt()
        val actionsY = ((collapsedHeightPx - actionsPlaceable?.height.orZero) / 2).fastRoundToInt()

        // Current coordinates of title
        var collapsingTitleY = 0
        var collapsingTitleX = 0

        if (expandedTitlePlaceable != null && collapsedTitlePlaceable != null) {
            // Measuring toolbar collapsing distance
            val heightOffsetLimitPx = expandedTitlePlaceable.height + expandedTitleBottomPaddingPx

            scrollBehavior?.state?.run {
                val oldLimit = heightOffsetLimit
                val fraction = if (oldLimit != 0f) heightOffset / -oldLimit else 0f

                heightOffsetLimit = when (topContent) {
                    null -> -heightOffsetLimitPx
                    else -> 0f
                }

                // Adjust heightOffset to maintain the same collapse fraction
                heightOffset = -fraction * heightOffsetLimit
            }

            // Toolbar height at fully expanded state
            val fullyExpandedHeightPx = collapsedHeightPx + heightOffsetLimitPx

            // Coordinates of fully expanded title
            val fullyExpandedTitleX = titleStartPaddingPx
            val fullyExpandedTitleY =
                fullyExpandedHeightPx - expandedTitlePlaceable.height - expandedTitleBottomPaddingPx

            // Coordinates of fully collapsed title
            val fullyCollapsedTitleX = navigationIconOffset
            val fullyCollapsedTitleY =
                (collapsedHeightPx - CollapsedTitleLineHeight.toPx().fastRoundToInt()) / 2

            // Current height of toolbar
            layoutHeightPx = lerp(fullyExpandedHeightPx, collapsedHeightPx, collapsedFraction).fastRoundToInt()

            // Current coordinates of collapsing title
            collapsingTitleX =
                lerp(fullyExpandedTitleX, fullyCollapsedTitleX, collapsedFraction).fastRoundToInt()
            collapsingTitleY =
                lerp(fullyExpandedTitleY, fullyCollapsedTitleY, collapsedFraction).fastRoundToInt()
        } else {
            scrollBehavior?.state?.heightOffsetLimit = -1f
        }

        val toolbarHeightPx = layoutHeightPx + bottomContentPlaceable?.height.orZero

        // Placing toolbar widgets:

        layout(constraints.maxWidth, toolbarHeightPx) {
            navigationIconPlaceable?.placeRelative(
                x = navigationIconX,
                y = navigationIconY
            )
            actionsPlaceable?.placeRelative(
                x = actionsX,
                y = actionsY
            )
            topContentPlaceable?.placeRelative(
                x = navigationIconOffset.fastRoundToInt(),
                y = ((collapsedHeightPx - topContentPlaceable.height) / 2).fastRoundToInt()
            )
            if (expandedTitlePlaceable?.width == collapsedTitlePlaceable?.width) {
                expandedTitlePlaceable?.placeRelativeWithLayer(
                    x = collapsingTitleX,
                    y = collapsingTitleY,
                ) {
                    scaleX = collapsingTitleScale
                    scaleY = collapsingTitleScale
                    transformOrigin = pivotPoint
                }
            } else {
                expandedTitlePlaceable?.placeRelativeWithLayer(
                    x = collapsingTitleX,
                    y = collapsingTitleY
                ) {
                    alpha = 1 - collapsedFraction
                    scaleX = collapsingTitleScale
                    scaleY = collapsingTitleScale
                    transformOrigin = pivotPoint
                }
                collapsedTitlePlaceable?.placeRelativeWithLayer(
                    x = collapsingTitleX,
                    y = collapsingTitleY
                ) {
                    alpha = collapsedFraction
                    scaleX = collapsingTitleScale
                    scaleY = collapsingTitleScale
                    transformOrigin = pivotPoint
                }
            }
            bottomContentPlaceable?.placeRelative(
                x = 0,
                y = layoutHeightPx
            )
        }
    }
}

private val Int?.orZero get() = this ?: 0

private val MinCollapsedHeight = 56.dp
private val ExpandedTitleBottomPadding = 8.dp
private val CollapsedTitleLineHeight = 28.sp

private const val ExpandedTitleId = "expandedTitle"
private const val CollapsedTitleId = "collapsedTitle"
private const val NavigationIconId = "navigationIcon"
private const val ActionsId = "actions"
private const val TopContentId = "topContent"
private const val BottomContentId = "bottomContent"
