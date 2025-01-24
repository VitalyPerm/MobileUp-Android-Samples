package ru.mobileup.samples.features.video.domain.events

sealed class PlayerEvent {
    data class PlayingStateChanged(val isPlaying: Boolean, val playFrom: Float) : PlayerEvent()
}