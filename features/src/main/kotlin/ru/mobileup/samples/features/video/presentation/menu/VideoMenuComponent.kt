package ru.mobileup.samples.features.video.presentation.menu

import ru.mobileup.samples.features.video.domain.VideoOption

interface VideoMenuComponent {

    fun onVideoOptionChosen(videoOption: VideoOption)

    sealed interface Output {
        data class VideoOptionChosen(val videoOption: VideoOption) : Output
    }
}