package ru.mobileup.samples.features.video.domain.events

sealed class VideoPlayerEvent {
    data class PlayingStateChanged(val isPlaying: Boolean, val playFrom: Float) : VideoPlayerEvent()
}