package ru.mobileup.samples.features.video.data.render.camera

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraEffect
import androidx.camera.core.SurfaceOutput
import androidx.camera.core.SurfaceProcessor
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.impl.utils.executor.CameraXExecutors.newHandlerExecutor
import androidx.core.util.Consumer
import androidx.core.util.Preconditions.checkState
import co.touchlab.kermit.Logger
import ru.mobileup.samples.features.video.data.render.GlFilter
import java.util.concurrent.Executor

private const val TAG = "ShaderCameraEffect"

interface ReleasableCameraEffect {
    fun release()
}

class ShaderCameraEffect private constructor(
    targets: Int,
    executor: Executor,
    private var surfaceProcessor: ShaderEffectSurfaceProcessor,
    errorListener: Consumer<Throwable>,
) : CameraEffect(targets, executor, surfaceProcessor, errorListener), ReleasableCameraEffect {

    companion object {
        fun create(
            targets: Int = PREVIEW or VIDEO_CAPTURE,
        ): ShaderCameraEffect {
            val surfaceProcessor = ShaderEffectSurfaceProcessor()
            return ShaderCameraEffect(
                targets = targets,
                executor = surfaceProcessor.glExecutor,
                surfaceProcessor = surfaceProcessor,
                errorListener = { e -> Logger.withTag(TAG).e(e.toString() + "errorListener") }
            )
        }
    }

    fun setGlFilter(glFilter: GlFilter) {
        surfaceProcessor.setGlFilter(glFilter)
    }

    override fun release() {
        surfaceProcessor.release()
    }
}

@SuppressLint("RestrictedApi")
private class ShaderEffectSurfaceProcessor : SurfaceProcessor,
    SurfaceTexture.OnFrameAvailableListener {

    companion object {
        private const val GL_THREAD_NAME = "ShaderEffectSurfaceProcessor"
    }

    private val glThread: HandlerThread = HandlerThread(GL_THREAD_NAME).apply { start() }
    private val glHandler: Handler = Handler(glThread.looper)
    val glExecutor: Executor = newHandlerExecutor(glHandler)

    private val openGlRenderer = CameraOpenGlRenderer()
    private val shaderEffectRenderer = ShaderEffectRenderer()
    private val outputSurfaces: MutableMap<SurfaceOutput, Surface> = mutableMapOf()
    private val textureTransform: FloatArray = FloatArray(16)
    private val surfaceTransform: FloatArray = FloatArray(16)
    private var textureSize = Size(0, 0)
    private var isReleased = false

    init {
        glExecutor.execute {
            openGlRenderer.setup()
            shaderEffectRenderer.setup()
        }
    }

    fun setGlFilter(glFilter: GlFilter) {
        glExecutor.execute {
            shaderEffectRenderer.setGlFilter(glFilter)
        }
    }

    override fun onInputSurface(surfaceRequest: SurfaceRequest) {
        checkGlThread()
        if (isReleased) {
            surfaceRequest.willNotProvideSurface()
            return
        }

        textureSize = Size(surfaceRequest.resolution.width, surfaceRequest.resolution.height)

        val surfaceTexture = SurfaceTexture(openGlRenderer.externalTextureId)
        surfaceTexture.setDefaultBufferSize(textureSize.width, textureSize.height)
        val surface = Surface(surfaceTexture)

        surfaceRequest.provideSurface(surface, glExecutor) {
            surfaceTexture.setOnFrameAvailableListener(null)
            surfaceTexture.release()
            surface.release()
        }

        surfaceTexture.setOnFrameAvailableListener(this, glHandler)
    }

    override fun onOutputSurface(surfaceOutput: SurfaceOutput) {
        checkGlThread()
        if (isReleased) {
            surfaceOutput.close()
            glThread.quitSafely()
            return
        }
        val surface = surfaceOutput.getSurface(glExecutor) {
            surfaceOutput.close()
            outputSurfaces.remove(surfaceOutput)?.let { removedSurface ->
                openGlRenderer.unregisterOutputSurface(removedSurface)
            }
        }
        openGlRenderer.registerOutputSurface(surface)
        outputSurfaces[surfaceOutput] = surface
    }

    fun release() {
        if (isReleased) return
        isReleased = true
        glExecutor.execute {
            releaseInternal()
        }
    }

    private fun releaseInternal() {
        checkGlThread()
        for (surfaceOutput in outputSurfaces.keys) {
            surfaceOutput.close()
        }
        outputSurfaces.clear()
        shaderEffectRenderer.release()
        openGlRenderer.release()
    }

    private fun checkGlThread() {
        checkState(GL_THREAD_NAME == Thread.currentThread().name)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        checkGlThread()
        if (isReleased) {
            return
        }

        surfaceTexture.updateTexImage()
        surfaceTexture.getTransformMatrix(textureTransform)
        for (entry in outputSurfaces.entries.iterator()) {
            val surface = entry.value
            val surfaceOutput = entry.key

            surfaceOutput.updateTransformMatrix(surfaceTransform, textureTransform)
            openGlRenderer.render(surfaceTexture.timestamp, surface) {
                shaderEffectRenderer.render(
                    openGlRenderer.externalTextureId,
                    surfaceTransform,
                    textureSize
                )
            }
        }
    }
}