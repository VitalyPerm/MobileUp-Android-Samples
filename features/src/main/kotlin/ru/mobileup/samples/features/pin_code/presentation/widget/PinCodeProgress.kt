package ru.mobileup.samples.features.pin_code.presentation.widget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.features.pin_code.domain.PinCode
import ru.mobileup.samples.features.pin_code.domain.PinCodeProgressState

private const val DOTS_ANIMATION_DURATION = 300L

@Composable
fun PinCodeProgress(
    pinProgressState: PinCodeProgressState,
    modifier: Modifier = Modifier,
    onDotsAnimationEnd: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            val coroutineScope = rememberCoroutineScope()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(PinCode.LENGTH) { index ->
                    ProgressDot(
                        pinProgressState = pinProgressState,
                        index = index,
                        onAnimationEnd = if (index + 1 == PinCode.LENGTH) {
                            onDotsAnimationEnd
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressDot(
    pinProgressState: PinCodeProgressState,
    index: Int,
    modifier: Modifier = Modifier,
    onAnimationEnd: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()
    val animatable = remember {
        Animatable(1f)
    }

    LaunchedEffect(pinProgressState) {
        when (pinProgressState) {
            PinCodeProgressState.Error -> coroutineScope.launch {
                animatable.animateDot()
                onAnimationEnd?.invoke()
            }
            PinCodeProgressState.Success -> coroutineScope.launch {
                animatable.animateDot()
                onAnimationEnd?.invoke()
            }

            is PinCodeProgressState.Progress -> {
                if (index + 1 == pinProgressState.count) {
                    coroutineScope.launch {
                        animatable.animateDot()
                        onAnimationEnd?.invoke()
                    }
                }
            }
        }
    }

    val color = when (pinProgressState) {
        PinCodeProgressState.Error -> {
            CustomTheme.colors.common.negative
        }

        is PinCodeProgressState.Progress -> {
            if (pinProgressState.count > index) {
                CustomTheme.colors.icon.primary
            } else {
                CustomTheme.colors.icon.secondary
            }
        }

        PinCodeProgressState.Success -> {
            CustomTheme.colors.common.positive
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatable.value
                scaleY = animatable.value
            }
            .clip(CircleShape)
            .size(12.dp)
            .background(color)
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.animateDot() {
    animateBackAndForth(
        initialValue = 1f,
        targetValue = 1.33f,
        animationSpec = tween(
            durationMillis = DOTS_ANIMATION_DURATION.toInt()
        )
    )
}

private suspend fun <T, V : AnimationVector> Animatable<T, V>.animateBackAndForth(
    initialValue: T,
    targetValue: T,
    animationSpec: AnimationSpec<T>
) {
    snapTo(initialValue)
    animateTo(targetValue, animationSpec)
    animateTo(initialValue, animationSpec)
}
