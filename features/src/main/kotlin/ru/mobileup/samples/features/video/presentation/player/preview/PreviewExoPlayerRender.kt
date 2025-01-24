package ru.mobileup.samples.features.video.presentation.player.preview

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import android.opengl.GLUtils
import android.opengl.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.Surface
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.image.ImageOutput
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.MVP_MATRIX
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_IMAGE_SIZE_FACTOR
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_INPUT_TEXTURE_COORDINATE
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_POSITION
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.TEX_MATRIX
import ru.mobileup.samples.features.video.data.utils.aspectRatio
import ru.mobileup.samples.features.video.domain.VideoTransform
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs

class PreviewExoPlayerRender : Renderer {

    private val matrices = Matrices()
    private val lock = Any()
    private val imageSizeFactor = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

    private var player: ExoPlayer? = null
    private var surfaceTexture: SurfaceTexture? = null

    @Volatile
    private var newVideoFrameAvailable: Boolean = false

    @Volatile
    private var newImageAvailable: Boolean = false

    @Volatile
    private var image: Bitmap? = null

    private var videoRenderResources: RenderResources? = null

    private var imageRenderResources: RenderResources? = null

    private var outputSize: Size = Size(0, 0)

    private var videoInputSize: Size = Size(0, 0)

    private var transform: VideoTransform = VideoTransform.defaultValue

    private val playerListener: Player.Listener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            if (videoSize.width > 0 && videoSize.height > 0) {
                videoInputSize = Size(videoSize.width, videoSize.height)
            }
        }
    }

    private val frameAvailableListener = SurfaceTexture.OnFrameAvailableListener {
        // Syncs are important because rendering happens on a separate thread
        synchronized(lock) {
            image = null
            newVideoFrameAvailable = true
        }
    }

    @OptIn(UnstableApi::class)
    private val imageOutput = object : ImageOutput {
        override fun onImageAvailable(presentationTimeUs: Long, bitmap: Bitmap) {
            synchronized(lock) {
                image = bitmap
                newImageAvailable = true
            }
        }

        override fun onDisabled() = Unit
    }

    var filter: GlFilter = GlFilter.Default
        private set

    @OptIn(UnstableApi::class)
    fun setExoPlayer(player: ExoPlayer) {
        if (this.player == player) return
        this.player?.removeListener(playerListener)
        this.player?.setImageOutput(ImageOutput.NO_OP)
        this.player = player
        player.addListener(playerListener)
        player.setImageOutput(imageOutput)
        setSurfaceToPlayer()
    }

    fun setFilter(filter: GlFilter) {
        this.filter = filter
    }

    fun setTransform(transform: VideoTransform) {
        this.transform = transform
    }

    fun release() {
        player?.clearVideoSurface()
        surfaceTexture?.release()
        surfaceTexture = null
        // The remaining resources do not need to be freed, as they are freed automatically when the EGLContext is destroyed
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Two versions of RenderResources are needed (for video and for image) because they have different texture formats and, accordingly, different shaders
        videoRenderResources = RenderResources.create(filter, forImage = false)
        imageRenderResources = RenderResources.create(filter, forImage = true)

        surfaceTexture = SurfaceTexture(videoRenderResources!!.textureId)
        synchronized(lock) {
            newVideoFrameAvailable =
                false // The player will notify about the frame in frameAvailableListener
            if (image != null) {
                // Image does not disappear after minimizing and maximizing the application
                newImageAvailable = true
            }
        }
        surfaceTexture?.setOnFrameAvailableListener(frameAvailableListener)
        setSurfaceToPlayer()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        outputSize = Size(width, height)
        matrices.updateProjectionMatrix(outputSize)
        gl.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        updateVideoTextureIfRequired()
        val image = updateImageTextureIfRequired()
        matrices.updateTransformMatrixIfRequired(transform, outputSize.aspectRatio())
        matrices.updateTransformProjectionMatrix()

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val useImage = image != null
        val renderResources = if (useImage) imageRenderResources else videoRenderResources
        val inputSize = if (useImage) Size(image!!.width, image.height) else videoInputSize

        if (renderResources != null) {
            renderResources.updateVerticesIfRequired(outputSize, inputSize)
            renderResources.updateShaderIfRequired(filter)
            draw(renderResources, inputSize)
        }

        GLES20.glFinish()
    }

    private fun setSurfaceToPlayer() {
        val action = {
            val surfaceTexture = this.surfaceTexture
            val player = this.player
            if (surfaceTexture != null && player != null) {
                val surface = Surface(surfaceTexture)
                player.clearVideoSurface()
                player.setVideoSurface(surface)
            }
        }

        // Must be called on main thread
        if (Looper.getMainLooper().isCurrentThread) {
            action()
        } else {
            Handler(Looper.getMainLooper()).post(action)
        }
    }

    private fun updateVideoTextureIfRequired() {
        synchronized(lock) {
            if (newVideoFrameAvailable) {
                surfaceTexture?.updateTexImage()
                surfaceTexture?.getTransformMatrix(matrices.videoTextureMatrix)
                newVideoFrameAvailable = false
            }
        }
    }

    private fun updateImageTextureIfRequired(): Bitmap? {
        val newImageAvailable: Boolean
        val image: Bitmap?
        synchronized(lock) {
            newImageAvailable = this.newImageAvailable
            image = this.image
            this.newImageAvailable = false
        }

        if (newImageAvailable && image != null) {
            val textureId = imageRenderResources?.textureId
            if (textureId != null) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0)
            }
        }

        return image
    }

    private fun updateImageSizeFactor(inputSize: Size, textureMatrix: FloatArray) {
        imageSizeFactor[0] = inputSize.width.toFloat()
        imageSizeFactor[1] = inputSize.height.toFloat()
        Matrix.multiplyMV(imageSizeFactor, 0, textureMatrix, 0, imageSizeFactor, 0)
        imageSizeFactor[0] = abs(imageSizeFactor[0])
        imageSizeFactor[1] = abs(imageSizeFactor[1])
    }

    private fun draw(renderResources: RenderResources, inputSize: Size) {
        val program = renderResources.shaderProgram
        GLES20.glUseProgram(program)

        val vertexHandle = GLES20.glGetAttribLocation(program, SHADER_POSITION)
        val uvsHandle = GLES20.glGetAttribLocation(program, SHADER_INPUT_TEXTURE_COORDINATE)
        val texMatrixHandle = GLES20.glGetUniformLocation(program, TEX_MATRIX)
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, MVP_MATRIX)
        val imageSizeFactorHandle = GLES20.glGetUniformLocation(program, SHADER_IMAGE_SIZE_FACTOR)

        // Bind
        val textureMatrix =
            if (renderResources.forImage) matrices.imageTextureMatrix else matrices.videoTextureMatrix
        GLES20.glUniformMatrix4fv(texMatrixHandle, 1, false, textureMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrices.transformProjectionMatrix, 0)
        if (imageSizeFactorHandle != -1) {
            updateImageSizeFactor(inputSize, textureMatrix)
            GLES20.glUniform2f(imageSizeFactorHandle, imageSizeFactor[0], imageSizeFactor[1])
        }
        GLES20.glBindTexture(
            if (renderResources.forImage) GLES20.GL_TEXTURE_2D else GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            renderResources.textureId
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, renderResources.vertexBufferId)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, renderResources.indexBufferId)
        GLES20.glEnableVertexAttribArray(vertexHandle)
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 4 * 5, 0)
        GLES20.glEnableVertexAttribArray(uvsHandle)
        GLES20.glVertexAttribPointer(uvsHandle, 2, GLES20.GL_FLOAT, false, 4 * 5, 3 * 4)

        // Draw
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_INT, 0)

        // Unbind
        GLES20.glDisableVertexAttribArray(vertexHandle)
        GLES20.glDisableVertexAttribArray(uvsHandle)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }
}

private data class RenderResources(
    val forImage: Boolean,
    val textureId: Int,
    val vertexBufferId: Int,
    val indexBufferId: Int,
    var shaderProgram: Int,

    // A set of values that, when changed, need to update vertexBufferId
    var vertexBufferInputs: Pair<Float, Float>? = null,

    // A set of values that, when changed, need to update the shaderProgram
    var shaderProgramInputs: GlFilter,
) {
    companion object {
        fun create(filter: GlFilter, forImage: Boolean): RenderResources {
            // Buffers
            val bufferHandles = IntArray(2)
            GLES20.glGenBuffers(2, bufferHandles, 0)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandles[0])
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandles[1])
            GLES20.glBufferData(
                GLES20.GL_ELEMENT_ARRAY_BUFFER,
                OpenGlUtils.INDICES.size * OpenGlUtils.FLOAT_SIZE_BYTES,
                OpenGlUtils.getIndicesBuffer(),
                GLES20.GL_DYNAMIC_DRAW
            )

            // Texture
            val textureHandles = IntArray(1)
            GLES20.glGenTextures(1, textureHandles, 0)
            GLES20.glBindTexture(
                if (forImage) GLES20.GL_TEXTURE_2D else GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                textureHandles[0]
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
            )

            // Shader program
            val shaderProgram = OpenGlUtils.createShaderProgram(
                OpenGlUtils.DEFAULT_VERTEX_SHADER,
                filter.getFullFragmentShader(externalTexture = !forImage)
            )

            return RenderResources(
                forImage = forImage,
                textureId = textureHandles[0],
                vertexBufferId = bufferHandles[0],
                indexBufferId = bufferHandles[1],
                shaderProgram = shaderProgram,
                shaderProgramInputs = filter
            )
        }
    }

    fun updateVerticesIfRequired(outputSize: Size, inputSize: Size) {
        val newVertexBufferInputs = Pair(outputSize.aspectRatio(), inputSize.aspectRatio())
        if (newVertexBufferInputs != vertexBufferInputs) {
            val vertices =
                OpenGlUtils.getVideoFrameVertices(surfaceSize = outputSize, inputSize = inputSize)
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId)
            GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                vertices.size * OpenGlUtils.FLOAT_SIZE_BYTES,
                OpenGlUtils.getVertexBuffer(vertices),
                GLES20.GL_DYNAMIC_DRAW
            )
            vertexBufferInputs = newVertexBufferInputs
        }
    }

    fun updateShaderIfRequired(filter: GlFilter) {
        if (filter != shaderProgramInputs) {
            GLES20.glDeleteProgram(shaderProgram)
            shaderProgram = OpenGlUtils.createShaderProgram(
                OpenGlUtils.DEFAULT_VERTEX_SHADER,
                filter.getFullFragmentShader(externalTexture = !forImage)
            )
            shaderProgramInputs = filter
        }
    }
}

private class Matrices(
    val videoTextureMatrix: FloatArray = FloatArray(16),
    val imageTextureMatrix: FloatArray = FloatArray(16),
    val projectionMatrix: FloatArray = FloatArray(16),
    val transformMatrix: FloatArray = FloatArray(16),
    val transformProjectionMatrix: FloatArray = FloatArray(16),

    // A set of values that, when changed, need to update transformMatrix
    var transformMatrixInputs: Pair<VideoTransform, Float>? = null,
) {
    init {
        Matrix.setIdentityM(videoTextureMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(transformMatrix, 0)
        Matrix.setIdentityM(transformProjectionMatrix, 0)
        imageTextureMatrix.let {
            Matrix.setIdentityM(it, 0)
            Matrix.translateM(it, 0, 0.0f, 1.0f, 0.0f)
            Matrix.scaleM(it, 0, 1.0f, -1.0f, 1.0f)
        }
    }

    fun updateTransformMatrixIfRequired(videoTransform: VideoTransform, outputAspectRatio: Float) {
        val newTransformInputs = Pair(videoTransform, outputAspectRatio)
        if (transformMatrixInputs != newTransformInputs) {
            val centerX = videoTransform.offsetPercent.x
            val centerY = -videoTransform.offsetPercent.y * (1f / outputAspectRatio)
            Matrix.setIdentityM(transformMatrix, 0)
            Matrix.translateM(transformMatrix, 0, centerX, centerY, 0f)
            Matrix.scaleM(transformMatrix, 0, videoTransform.scale, videoTransform.scale, 0f)
            Matrix.rotateM(transformMatrix, 0, -videoTransform.rotation, 0f, 0f, 1f)
            transformMatrixInputs = newTransformInputs
        }
    }

    fun updateProjectionMatrix(outputSize: Size) {
        val aspectRatio = outputSize.aspectRatio()
        Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -1f / aspectRatio, 1f / aspectRatio, -1f, 1f)
    }

    fun updateTransformProjectionMatrix() {
        Matrix.setIdentityM(transformProjectionMatrix, 0)
        Matrix.multiplyMM(
            transformProjectionMatrix, 0, transformMatrix, 0, transformProjectionMatrix, 0
        )
        Matrix.multiplyMM(
            transformProjectionMatrix, 0, projectionMatrix, 0, transformProjectionMatrix, 0
        )
    }
}