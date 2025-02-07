package ru.mobileup.samples.features.video.data.render.camera

import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Size
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_IMAGE_SIZE_FACTOR
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_INPUT_TEXTURE
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_INPUT_TEXTURE_COORDINATE
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_POSITION
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.SHADER_TEXTURE_COORDINATE
import ru.mobileup.samples.features.video.data.utils.OpenGlUtils.TEX_MATRIX
import ru.mobileup.samples.features.video.data.utils.checkGlErrorOrThrow
import ru.mobileup.samples.features.video.data.utils.createFloatBuffer
import ru.mobileup.samples.features.video.data.utils.createProgram

class ShaderEffectRenderer {
    companion object {

        private val VERTEX_SHADER = """
            uniform mat4 $TEX_MATRIX;

            attribute vec4 $SHADER_POSITION;
            attribute vec4 $SHADER_INPUT_TEXTURE_COORDINATE;
            varying vec2 $SHADER_TEXTURE_COORDINATE;
            
            void main() {
                gl_Position = $SHADER_POSITION;
                vTextureCoord = ($TEX_MATRIX * $SHADER_INPUT_TEXTURE_COORDINATE).xy;
            }
        """.trimIndent()

        private fun getFragmentShaderCode(filter: GlFilter): String {
            return """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            
            uniform samplerExternalOES $SHADER_INPUT_TEXTURE;
            varying vec2 $SHADER_TEXTURE_COORDINATE;
            uniform vec2 $SHADER_IMAGE_SIZE_FACTOR;

            ${filter.fragmentShader}
            """
        }

        private val VERTEX_COORDS = floatArrayOf(
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
        )

        private val VERTEX_BUF = createFloatBuffer(VERTEX_COORDS)

        private val TEX_COORDS = floatArrayOf(
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
        )

        private val TEX_BUF = createFloatBuffer(TEX_COORDS)
    }

    private var programHandle = -1
    private var texMatrixLoc = -1
    private var positionLoc = -1
    private var texCoordLoc = -1
    private var imageSizeFactorLoc = -1

    private var glFilter: GlFilter = GlFilter.Default

    fun setup() {
        createShader(glFilter)
    }

    fun setGlFilter(glFilter: GlFilter) {
        if (this.glFilter != glFilter) {
            this.glFilter = glFilter
            releaseShader()
            createShader(glFilter)
        }
    }

    fun render(
        textureId: Int,
        textureTransform: FloatArray,
        textureSize: Size,
    ) {
        useAndConfigureShader(textureId, textureTransform, textureSize)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        checkGlErrorOrThrow("glDrawArrays")
    }

    fun release() {
        releaseShader()
    }

    private fun createShader(glFilter: GlFilter) {
        programHandle = createProgram(VERTEX_SHADER, getFragmentShaderCode(glFilter))

        positionLoc = GLES20.glGetAttribLocation(programHandle, SHADER_POSITION)
        texCoordLoc = GLES20.glGetAttribLocation(programHandle, SHADER_INPUT_TEXTURE_COORDINATE)
        texMatrixLoc = GLES20.glGetUniformLocation(programHandle, TEX_MATRIX)
        imageSizeFactorLoc = GLES20.glGetUniformLocation(programHandle, SHADER_IMAGE_SIZE_FACTOR)
    }

    private fun releaseShader() {
        if (programHandle != -1) {
            GLES20.glDeleteProgram(programHandle)
            programHandle = -1
        }

        positionLoc = -1
        texCoordLoc = -1
        texMatrixLoc = -1
        imageSizeFactorLoc = -1
    }

    private fun useAndConfigureShader(
        textureId: Int,
        textureTransform: FloatArray,
        textureSize: Size,
    ) {
        // Set program
        GLES20.glUseProgram(programHandle)

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

        // Set vertices
        GLES20.glEnableVertexAttribArray(positionLoc)
        GLES20.glVertexAttribPointer(
            positionLoc, 2, GLES20.GL_FLOAT, false, 0, VERTEX_BUF
        )
        GLES20.glEnableVertexAttribArray(texCoordLoc)
        GLES20.glVertexAttribPointer(
            texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, TEX_BUF
        )

        // Set matrix
        GLES20.glUniformMatrix4fv(
            texMatrixLoc, 1, false, textureTransform, 0
        )

        // Set image size factor
        GLES20.glUniform2f(
            imageSizeFactorLoc,
            textureSize.width.toFloat(),
            textureSize.height.toFloat()
        )

        checkGlErrorOrThrow("after useAndConfigureShader")
    }
}