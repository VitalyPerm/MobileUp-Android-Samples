package ru.mobileup.samples.features.video.data.render.transformer

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.INDICES
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.createShaderProgram
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.getVertexBuffer
import ru.mobileup.samples.features.video.data.utils.aspectRatio
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

@UnstableApi
internal abstract class TextureRenderShaderProgram(
    surfaceSize: Size,
) : BaseGlShaderProgram(false, 1) {

    private var program: Int = 0
    private var vertexHandle: Int = 0
    private var bufferHandles = IntArray(2)
    private var uvsHandle: Int = 0
    private var samplerHandle: Int = 0

    protected val transformMatrix = FloatArray(16).apply {
        Matrix.setIdentityM(this, 0)
    }
    protected val projectionMatrix = FloatArray(16)

    protected val surfaceAspectRatio: Float = surfaceSize.aspectRatio()

    private val indexBuffer: IntBuffer
        get() = ByteBuffer.allocateDirect(INDICES.size * 4).run {
            order(ByteOrder.nativeOrder())
            asIntBuffer().apply {
                put(INDICES)
                position(0)
            }
        }

    protected fun createProgram(
        vertexShader: String,
        fragmentShader: String,
    ) {
        Matrix.orthoM(
            projectionMatrix,
            0,
            -1f,
            1f,
            -1f / surfaceAspectRatio,
            1f / surfaceAspectRatio,
            -1f,
            1f
        )
        // Create program
        program = createShaderProgram(
            vertexShaderSource = vertexShader,
            fragmentShaderSource = fragmentShader
        )

        // Initialize buffers
        GLES20.glGenBuffers(2, bufferHandles, 0)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandles[1])
        GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER,
            INDICES.size * OpenGlUtils.FLOAT_SIZE_BYTES,
            indexBuffer,
            GLES20.GL_DYNAMIC_DRAW
        )

        handleAttributes(program)
    }

    protected open fun handleAttributes(program: Int) {
        vertexHandle = GLES20.glGetAttribLocation(program, OpenGlUtils.SHADER_POSITION)
        uvsHandle = GLES20.glGetAttribLocation(program, OpenGlUtils.SHADER_INPUT_TEXTURE_COORDINATE)
        samplerHandle = GLES20.glGetUniformLocation(program, OpenGlUtils.SHADER_INPUT_TEXTURE)
    }

    protected abstract fun updateAttributesMatrix(program: Int)

    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long) {
        GLES20.glUseProgram(program)

        updateAttributesMatrix(program)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTexId)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandles[0])
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferHandles[1])

        GLES20.glEnableVertexAttribArray(vertexHandle)
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 4 * 5, 0)
        GLES20.glEnableVertexAttribArray(uvsHandle)
        GLES20.glVertexAttribPointer(uvsHandle, 2, GLES20.GL_FLOAT, false, 4 * 5, 3 * 4)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, INDICES.size, GLES20.GL_UNSIGNED_INT, 0)

        GLES20.glDisableVertexAttribArray(vertexHandle)
        GLES20.glDisableVertexAttribArray(uvsHandle)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    protected fun updateVertices(vertices: FloatArray) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferHandles[0])
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            vertices.size * OpenGlUtils.FLOAT_SIZE_BYTES,
            getVertexBuffer(vertices),
            GLES20.GL_DYNAMIC_DRAW
        )
    }

    override fun release() {
        super.release()
        GLES20.glDeleteProgram(program)
        GLES20.glDeleteBuffers(2, bufferHandles, 0)
    }
}