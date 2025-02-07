package ru.mobileup.samples.features.video.data.render.transformer

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Size
import androidx.media3.common.util.GlUtil
import androidx.media3.common.util.UnstableApi
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.DEFAULT_VERTEX_SHADER
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.getVideoFrameVertices
import ru.mobileup.samples.features.video.domain.VideoTransform
import kotlin.math.abs

@UnstableApi
internal class VideoTextureRenderShaderProgram(
    private val inputSize: Size,
    private val surfaceSize: Size,
    private val onProgress: (Float) -> Unit,
    filter: GlFilter,
    transform: VideoTransform,
) : TextureRenderShaderProgram(surfaceSize = surfaceSize) {

    private var texMatrixHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var imageSizeFactorHandle: Int = 0
    private val imageSizeFactor = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

    private val videoTextureMatrix = GlUtil.create4x4IdentityMatrix()

    init {
        setTransformMatrices(transform = transform)
        createProgram(
            vertexShader = DEFAULT_VERTEX_SHADER,
            fragmentShader = filter.getFullFragmentShader(false)
        )
    }

    override fun updateAttributesMatrix(program: Int) {
        GLES20.glUniformMatrix4fv(texMatrixHandle, 1, false, videoTextureMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, getMVP(), 0)
        if (imageSizeFactorHandle != -1) {
            updateImageSizeFactor(
                inputSize = Size(inputSize.width, inputSize.height),
                textureMatrix = videoTextureMatrix
            )
            GLES20.glUniform2f(imageSizeFactorHandle, imageSizeFactor[0], imageSizeFactor[1])
        }
    }

    override fun configure(inputWidth: Int, inputHeight: Int): androidx.media3.common.util.Size {
        updateVertices(getVideoFrameVertices(surfaceSize, inputSize))
        return androidx.media3.common.util.Size(inputWidth, inputHeight)
    }

    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long) {
        super.drawFrame(inputTexId, presentationTimeUs)
        onProgress((presentationTimeUs / 1_000_000).toFloat())
    }

    override fun handleAttributes(program: Int) {
        super.handleAttributes(program)
        texMatrixHandle = GLES20.glGetUniformLocation(program, OpenGlUtils.TEX_MATRIX)
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, OpenGlUtils.MVP_MATRIX)
        imageSizeFactorHandle =
            GLES20.glGetUniformLocation(program, OpenGlUtils.SHADER_IMAGE_SIZE_FACTOR)
    }

    private fun getMVP(): FloatArray {
        val mvp = FloatArray(16)
        Matrix.setIdentityM(mvp, 0)
        Matrix.multiplyMM(mvp, 0, transformMatrix, 0, mvp, 0)
        Matrix.multiplyMM(mvp, 0, projectionMatrix, 0, mvp, 0)
        return mvp
    }

    private fun setTransformMatrices(transform: VideoTransform) {
        val centerX = transform.offsetPercent.x
        val centerY = -transform.offsetPercent.y * (1f / surfaceAspectRatio)
        Matrix.translateM(transformMatrix, 0, centerX, centerY, 0f)
        Matrix.scaleM(transformMatrix, 0, transform.scale, transform.scale, 0f)
        Matrix.rotateM(transformMatrix, 0, -transform.rotation, 0f, 0f, 1f)
    }

    private fun updateImageSizeFactor(inputSize: Size, textureMatrix: FloatArray) {
        imageSizeFactor[0] = inputSize.width.toFloat()
        imageSizeFactor[1] = inputSize.height.toFloat()
        Matrix.multiplyMV(imageSizeFactor, 0, textureMatrix, 0, imageSizeFactor, 0)
        imageSizeFactor[0] = abs(imageSizeFactor[0])
        imageSizeFactor[1] = abs(imageSizeFactor[1])
    }
}