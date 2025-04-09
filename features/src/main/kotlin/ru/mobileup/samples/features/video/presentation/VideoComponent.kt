package ru.mobileup.samples.features.video.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.video.presentation.menu.VideoMenuComponent
import ru.mobileup.samples.features.video.presentation.player.VideoPlayerComponent
import ru.mobileup.samples.features.video.presentation.recorder.VideoRecorderComponent

interface VideoComponent : BackHandlerOwner {

    val childStack: StateFlow<ChildStack<*, Child>>

    fun onBackClick()

    sealed interface Child {
        class Menu(val component: VideoMenuComponent) : Child
        class Recorder(val component: VideoRecorderComponent) : Child
        class Player(val component: VideoPlayerComponent) : Child
    }
}