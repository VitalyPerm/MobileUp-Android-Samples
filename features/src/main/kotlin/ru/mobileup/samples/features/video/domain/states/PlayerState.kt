package ru.mobileup.samples.features.video.domain.states

import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.domain.VideoTransform
import ru.mobileup.samples.features.video.presentation.player.preview.PlayerOrientation

data class PlayerState(
    val startPositionMs: Long,
    val endPositionMs: Long,
    val maxDurationMs: Long,
    val volume: Float,
    val speed: Float,
    val videoTransform: VideoTransform,
    val glFilter: GlFilter,
    val renderProgress: Float?,
    val orientation: PlayerOrientation,
) {
    companion object {

        fun build(durationMs: Long = 10_000) = PlayerState(
            startPositionMs = 0L,
            endPositionMs = durationMs,
            maxDurationMs = durationMs,
            volume = 1f,
            speed = 1f,
            videoTransform = VideoTransform(),
            glFilter = GlFilter.Default,
            renderProgress = null,
            orientation = PlayerOrientation.Portrait
        )
    }

    val duration: Long get() = ((endPositionMs - startPositionMs) / speed).toLong()
}