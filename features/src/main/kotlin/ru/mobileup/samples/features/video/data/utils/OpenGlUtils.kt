package ru.mobileup.samples.features.video.data.utils

import android.opengl.GLES20
import android.util.Size
import co.touchlab.kermit.Logger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

object OpenGlUtils {
    const val SHADER_INPUT_TEXTURE = "sTexture"
    const val SHADER_TEXTURE_COORDINATE = "vTextureCoord"
    const val SHADER_INPUT_TEXTURE_COORDINATE = "aTextureCoord"
    const val SHADER_POSITION = "aPosition"
    const val SHADER_IMAGE_SIZE_FACTOR = "imageSizeFactor"
    const val TEX_MATRIX = "uSTMatrix"
    const val MVP_MATRIX = "uMVPMatrix"

    const val FLOAT_SIZE_BYTES: Int = 4

    const val DEFAULT_VERTEX_SHADER: String = (
            "uniform mat4 $MVP_MATRIX;\n" +
                    "uniform mat4 $TEX_MATRIX;\n" +
                    "attribute vec4 $SHADER_POSITION;\n" +
                    "attribute vec4 $SHADER_INPUT_TEXTURE_COORDINATE;\n" +
                    "varying vec2 $SHADER_TEXTURE_COORDINATE;\n" +
                    "void main() {\n" +
                    "  gl_Position = $MVP_MATRIX * $SHADER_POSITION;\n" +
                    "  $SHADER_TEXTURE_COORDINATE = ($TEX_MATRIX * $SHADER_INPUT_TEXTURE_COORDINATE).xy;\n" +
                    "}\n"
            )

    const val OVERLAY_VERTEX_SHADER_CODE =
        """                       
        precision highp float;
        attribute vec4 $SHADER_POSITION;
        attribute vec2 $SHADER_INPUT_TEXTURE_COORDINATE;
        varying vec2 $SHADER_TEXTURE_COORDINATE;
        
        void main() {
            $SHADER_TEXTURE_COORDINATE = $SHADER_INPUT_TEXTURE_COORDINATE;
            gl_Position = $SHADER_POSITION;
        }
        """

    const val OVERLAY_FRAGMENT_SHADER_CODE =
        """
        precision mediump float;         
        varying vec2 $SHADER_TEXTURE_COORDINATE;
        uniform sampler2D $SHADER_INPUT_TEXTURE;
        
        void main() {
            gl_FragColor = texture2D($SHADER_INPUT_TEXTURE, $SHADER_TEXTURE_COORDINATE);
        }
        """

    val INDICES = intArrayOf(2, 1, 0, 0, 3, 2)

    private fun loadShader(shaderType: Int, source: String?): Int {
        var shader = GLES20.glCreateShader(shaderType)
        if (shader != 0) {
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(
                shader, GLES20.GL_COMPILE_STATUS,
                compiled, 0
            )
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    fun createShaderProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        var program = GLES20.glCreateProgram()
        if (program != 0) {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)
            val linkStatus = IntArray(1)
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    fun getVertexBuffer(vertices: FloatArray): FloatBuffer {
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
        return vertexBuffer
    }

    fun getIndicesBuffer(): IntBuffer {
        val indexBuffer = ByteBuffer.allocateDirect(INDICES.size * 4)
            .order(ByteOrder.nativeOrder()).asIntBuffer()
        indexBuffer.put(INDICES)
        indexBuffer.position(0)
        return indexBuffer
    }

    /**
     * Needed for correct placement of input video
     */
    fun getVideoFrameVertices(
        surfaceSize: Size,
        inputSize: Size,
    ): FloatArray {
        return getVideoFrameVertices(
            surfaceAspectRatio = surfaceSize.aspectRatio(),
            inputSizeAspectRatio = inputSize.aspectRatio()
        )
    }

    fun getVideoFrameVertices(
        surfaceAspectRatio: Float,
        inputSizeAspectRatio: Float,
    ): FloatArray {
        val isHorizontalOutput = surfaceAspectRatio > 1
        val isHorizontalInput = inputSizeAspectRatio > 1
        val x: Float
        val y: Float
        when {
            isHorizontalOutput -> {
                when {
                    inputSizeAspectRatio > surfaceAspectRatio -> {
                        x = 1f
                        y = surfaceAspectRatio / inputSizeAspectRatio
                    }
                    else -> {
                        y = 1f / surfaceAspectRatio
                        x = inputSizeAspectRatio * y
                    }
                }
            }
            else -> {
                when {
                    inputSizeAspectRatio <= surfaceAspectRatio || isHorizontalInput -> {
                        x = 1f
                        y = x / inputSizeAspectRatio
                    }

                    else -> {
                        y = 1f / surfaceAspectRatio
                        x = inputSizeAspectRatio * y
                    }
                }
            }
        }
        val vertices = floatArrayOf(
            // x, y, z, u, v
            x, y, 0.0f, 1f, 1f,
            x, -y, 0.0f, 1f, 0f,
            -x, -y, 0.0f, 0f, 0f,
            -x, y, 0.0f, 0f, 1f,
        )
        return vertices
    }

    /**
     * Needed for correct placement of overlay bitmap
     */
    internal fun getOverlayVertices() =
        floatArrayOf(
            // x, y, z, u, v
            1.0f, 1.0f, 0.0f, 1f, 1f,
            1.0f, -1.0f, 0.0f, 1f, 0f,
            -1.0f, -1.0f, 0.0f, 0f, 0f,
            -1.0f, 1.0f, 0.0f, 0f, 1f,
        )
}

fun createProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
    var vertexShader = -1
    var fragmentShader = -1
    var program = -1
    try {
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource)
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)
        program = GLES20.glCreateProgram()
        checkGlErrorOrThrow("glCreateProgram")
        GLES20.glAttachShader(program, vertexShader)
        checkGlErrorOrThrow("glAttachShader")
        GLES20.glAttachShader(program, fragmentShader)
        checkGlErrorOrThrow("glAttachShader")
        GLES20.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        check(linkStatus[0] == GLES20.GL_TRUE) {
            "Could not link program: " + GLES20.glGetProgramInfoLog(program)
        }
        return program
    } catch (e: IllegalStateException) {
        if (vertexShader != -1) {
            GLES20.glDeleteShader(vertexShader)
        }
        if (fragmentShader != -1) {
            GLES20.glDeleteShader(fragmentShader)
        }
        if (program != -1) {
            GLES20.glDeleteProgram(program)
        }
        throw e
    } catch (e: IllegalArgumentException) {
        if (vertexShader != -1) {
            GLES20.glDeleteShader(vertexShader)
        }
        if (fragmentShader != -1) {
            GLES20.glDeleteShader(fragmentShader)
        }
        if (program != -1) {
            GLES20.glDeleteProgram(program)
        }
        throw e
    }
}

fun loadShader(shaderType: Int, source: String): Int {
    val shader = GLES20.glCreateShader(shaderType)
    checkGlErrorOrThrow("glCreateShader type=$shaderType")
    GLES20.glShaderSource(shader, source)
    GLES20.glCompileShader(shader)
    val compiled = IntArray(1)
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
    if (compiled[0] == 0) {
        Logger.withTag("OpenGlUtils").e("Could not compile shader: $source")
        GLES20.glDeleteShader(shader)
        throw IllegalStateException(
            "Could not compile shader type $shaderType:" + GLES20.glGetShaderInfoLog(shader)
        )
    }
    return shader
}

fun checkGlErrorOrThrow(op: String) {
    val error = GLES20.glGetError()
    if (error != GLES20.GL_NO_ERROR) {
        throw IllegalStateException(op + ": GL error 0x" + Integer.toHexString(error))
    }
}

fun createFloatBuffer(coords: FloatArray): FloatBuffer {
    val bb = ByteBuffer.allocateDirect(coords.size * 4)
    bb.order(ByteOrder.nativeOrder())
    val fb = bb.asFloatBuffer()
    fb.put(coords)
    fb.position(0)
    return fb
}