package ru.mobileup.samples.features.video.data.render.transformer

import android.net.Uri
import android.util.Size
import kotlinx.coroutines.flow.Flow
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.domain.states.RenderState
import ru.mobileup.samples.features.video.domain.VideoTransform

interface VideoRender {

    fun execute(
        uri: Uri,
        startPositionMs: Long,
        endPositionMs: Long,
        volume: Float,
        speed: Float,
        size: Size,
        videoTransform: VideoTransform,
        glFilter: GlFilter
    ): Flow<RenderState>

    fun cancel()
}