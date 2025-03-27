package ru.mobileup.samples.features.collapsing_toolbar.presentation.common.widget

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

/**
 * The implementation is fully taken from [androidx.compose.material3.ExitUntilCollapsedScrollBehavior].
 */
class CustomToolbarScrollBehavior(
    val state: CustomToolbarState,
    val snapAnimationSpec: AnimationSpec<Float>,
    val flingAnimationSpec: DecayAnimationSpec<Float>,
    val canCollapse: () -> Boolean,
    val convergenceCoefficient: () -> Float
) {

    val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Don't intercept if scrolling down.
            if (!canCollapse() || available.y > 0f) return Offset.Zero

            val prevHeightOffset = state.heightOffset
            state.heightOffset += available.y
            return if (prevHeightOffset != state.heightOffset) {
                // We're in the middle of top app bar collapse or expand.
                // Consume only the scroll on the Y axis.
                available.copy(
                    x = 0f,
                    y = available.y * (1f - convergenceCoefficient().coerceIn(0f, 1f))
                )
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (!canCollapse()) return Offset.Zero
            state.contentOffset += consumed.y

            if (available.y < 0f || consumed.y < 0f) {
                // When scrolling up, just update the state's height offset.
                val oldHeightOffset = state.heightOffset
                state.heightOffset += consumed.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }

            if (consumed.y == 0f && available.y > 0) {
                // Reset the total content offset to zero when scrolling all the way down. This
                // will eliminate some float precision inaccuracies.
                state.contentOffset = 0f
            }

            if (available.y > 0f) {
                // Adjust the height offset in case the consumed delta Y is less than what was
                // recorded as available delta Y in the pre-scroll.
                val oldHeightOffset = state.heightOffset
                state.heightOffset += available.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            var result = super.onPostFling(consumed, available)
            // Check if the app bar is partially collapsed/expanded.
            // Note that we don't check for 0f due to float precision with the collapsedFraction
            // calculation.
            if (state.collapsedFraction > 0.01f && state.collapsedFraction < 1f) {
                result += flingToolbar(
                    state = state,
                    initialVelocity = available.y,
                    flingAnimationSpec = flingAnimationSpec
                )
                snapToolbar(state, snapAnimationSpec)
            }
            return result
        }
    }
}

private suspend fun flingToolbar(
    state: CustomToolbarState,
    initialVelocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
): Velocity {
    var remainingVelocity = initialVelocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(initialVelocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    return Velocity(0f, remainingVelocity)
}

private suspend fun snapToolbar(
    state: CustomToolbarState,
    snapAnimationSpec: AnimationSpec<Float>,
) {
    // In case the app bar motion was stopped in a state where it's partially visible, snap it to
    // the nearest state.
    if (state.heightOffset < 0 &&
        state.heightOffset > state.heightOffsetLimit
    ) {
        AnimationState(
            initialValue = state.heightOffset
        ).animateTo(
            targetValue = if (state.collapsedFraction < 0.5f) 0f else state.heightOffsetLimit,
            animationSpec = snapAnimationSpec
        ) {
            state.heightOffset = value
        }
    }
}
