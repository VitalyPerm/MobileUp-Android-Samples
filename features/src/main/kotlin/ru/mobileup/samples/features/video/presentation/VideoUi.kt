package ru.mobileup.samples.features.video.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.video.presentation.menu.VideoMenuUi
import ru.mobileup.samples.features.video.presentation.player.VideoPlayerUi
import ru.mobileup.samples.features.video.presentation.recorder.VideoRecorderUi

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun VideoUi(
    component: VideoComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.childStack.collectAsState()

    Children(
        modifier = modifier,
        stack = childStack,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(fade() + slide()),
            selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
            onBack = component::onBackClick,
        ),
    ) { child ->
        when (val instance = child.instance) {
            is VideoComponent.Child.Menu -> VideoMenuUi(instance.component)
            is VideoComponent.Child.Recorder -> VideoRecorderUi(instance.component)
            is VideoComponent.Child.Player -> VideoPlayerUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun VideoUiPreview() {
    AppTheme {
        VideoUi(FakeVideoComponent())
    }
}