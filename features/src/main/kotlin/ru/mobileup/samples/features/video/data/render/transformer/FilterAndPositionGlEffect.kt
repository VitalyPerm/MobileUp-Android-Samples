package ru.mobileup.samples.features.video.data.render.transformer

import android.content.Context
import android.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.domain.VideoTransform

@UnstableApi
class FilterAndPositionGlEffect(
    private val glFilter: GlFilter,
    private val inputSize: Size,
    private val outputSize: Size,
    private val videoTransform: VideoTransform,
    private val onProgress: (Float) -> Unit,
) : GlEffect {

    override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram {
        return VideoTextureRenderShaderProgram(
            inputSize = inputSize,
            surfaceSize = outputSize,
            onProgress = onProgress,
            filter = glFilter,
            transform = videoTransform
        )
    }
}