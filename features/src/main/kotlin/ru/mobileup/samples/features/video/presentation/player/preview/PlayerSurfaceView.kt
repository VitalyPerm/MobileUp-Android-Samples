package ru.mobileup.samples.features.video.presentation.player.preview

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import androidx.media3.exoplayer.ExoPlayer
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.domain.VideoTransform

class PlayerSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : GLSurfaceView(context, attrs) {

    private val renderer = PreviewExoPlayerRender()

    val glFilter: GlFilter
        get() = renderer.filter

    init {
        preserveEGLContextOnPause = false
        setEGLContextClientVersion(2)
        setRenderer(renderer)
    }

    fun setExoPlayer(player: ExoPlayer) {
        renderer.setExoPlayer(player)
    }

    fun setFilter(glFilter: GlFilter) {
        renderer.setFilter(glFilter)
    }

    fun setTransform(transform: VideoTransform) {
        renderer.setTransform(transform)
    }

    override fun onPause() {
        super.onPause()
        renderer.release() // onPause destroys the EGLContext, so some resources in the renderer need to be freed
    }
}