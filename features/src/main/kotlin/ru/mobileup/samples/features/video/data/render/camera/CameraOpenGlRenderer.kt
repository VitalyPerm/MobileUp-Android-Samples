package ru.mobileup.samples.features.video.data.render.camera

import android.annotation.SuppressLint
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import androidx.camera.core.Logger
import androidx.core.util.Preconditions
import java.util.concurrent.atomic.AtomicBoolean

/**
 * CameraOpenGlRenderer renders content to the output surface.
 *
 * CameraOpenGlRenderer's methods must run on the same thread, so called GL thread. The GL thread is
 * locked as the thread running the [.init] method, otherwise an
 * [IllegalStateException] will be thrown when other methods are called.
 */
@SuppressLint("RestrictedApi")
@WorkerThread
class CameraOpenGlRenderer {
    private val mInitialized = AtomicBoolean(false)

    @VisibleForTesting
    val mOutputSurfaceMap: MutableMap<Surface, OutputSurface> = HashMap()
    private var mGlThread: Thread? = null
    private var mEglDisplay = EGL14.EGL_NO_DISPLAY
    private var mEglContext = EGL14.EGL_NO_CONTEXT
    private var mEglConfig: EGLConfig? = null
    private var mTempSurface = EGL14.EGL_NO_SURFACE
    private var mCurrentSurface: Surface? = null
    private var mExternalTextureId = -1

    /**
     * Initializes the CameraOpenGLRenderer
     * Initialization must be done before calling other methods, otherwise an
     * [IllegalStateException] will be thrown. Following methods must run on the same
     * thread as this method, so called GL thread, otherwise an [IllegalStateException]
     * will be thrown.
     *
     * @throws IllegalStateException if the renderer is already initialized or failed to be
     * initialized.
     * @throws IllegalArgumentException if the ShaderProvider fails to create shader or provides
     * invalid shader string.
     */
    fun setup() {
        checkInitializedOrThrow(false)

        try {
            createEglContext()
            createTempSurface()
            makeCurrent(mTempSurface)
            createExternalTexture()
        } catch (e: IllegalStateException) {
            releaseInternal()
            throw e
        } catch (e: IllegalArgumentException) {
            releaseInternal()
            throw e
        }
        mGlThread = Thread.currentThread()
        mInitialized.set(true)
    }

    /**
     * Releases the CameraOpenGLRenderer
     *
     * @throws IllegalStateException if the caller doesn't run on the GL thread.
     */
    fun release() {
        if (!mInitialized.getAndSet(false)) {
            return
        }
        checkGlThreadOrThrow()
        releaseInternal()
    }

    /**
     * Register the output surface.
     *
     * @throws IllegalStateException if the renderer is not initialized or the caller doesn't run
     * on the GL thread.
     */
    fun registerOutputSurface(surface: Surface) {
        checkInitializedOrThrow(true)
        checkGlThreadOrThrow()
        if (!mOutputSurfaceMap.containsKey(surface)) {
            mOutputSurfaceMap[surface] = NO_OUTPUT_SURFACE
        }
    }

    /**
     * Unregister the output surface.
     *
     * @throws IllegalStateException if the renderer is not initialized or the caller doesn't run
     * on the GL thread.
     */
    fun unregisterOutputSurface(surface: Surface) {
        checkInitializedOrThrow(true)
        checkGlThreadOrThrow()
        removeOutputSurfaceInternal(surface, true)
    }

    val externalTextureId: Int
        /**
         * Gets the texture name.
         *
         * @return the texture name
         * @throws IllegalStateException if the renderer is not initialized or the caller doesn't run
         * on the GL thread.
         */
        get() {
            checkInitializedOrThrow(true)
            checkGlThreadOrThrow()
            return mExternalTextureId
        }

    /**
     * Renders the texture image to the output surface.
     *
     * @throws IllegalStateException if the renderer is not initialized, the caller doesn't run
     * on the GL thread or the surface is not registered by
     * [.registerOutputSurface].
     */
    fun render(
        timestampNs: Long,
        surface: Surface,
        renderContent: () -> Unit,
    ) {
        checkInitializedOrThrow(true)
        checkGlThreadOrThrow()
        var outputSurface: OutputSurface? = getOutSurfaceOrThrow(surface)

        // Workaround situations that out surface is failed to create or needs to be recreated.
        if (outputSurface === NO_OUTPUT_SURFACE) {
            outputSurface = createOutputSurfaceInternal(surface)
            if (outputSurface == null) {
                return
            }
            mOutputSurfaceMap[surface] = outputSurface
        }

        // Set output surface.
        if (surface !== mCurrentSurface) {
            makeCurrent(outputSurface!!.eglSurface)
            mCurrentSurface = surface
        }

        if (outputSurface != null) {
            GLES20.glViewport(0, 0, outputSurface.width, outputSurface.height)
            GLES20.glScissor(0, 0, outputSurface.width, outputSurface.height)
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        renderContent()

        // Set timestamp
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, outputSurface!!.eglSurface, timestampNs)

        // Swap buffer
        if (!EGL14.eglSwapBuffers(mEglDisplay, outputSurface.eglSurface)) {
            Logger.w(
                TAG, "Failed to swap buffers with EGL error: 0x" + Integer.toHexString(
                    EGL14.eglGetError()
                )
            )
            removeOutputSurfaceInternal(surface, false)
        }
    }

    private fun createEglContext() {
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        check(mEglDisplay != EGL14.EGL_NO_DISPLAY) { "Unable to get EGL14 display" }
        val version = IntArray(2)
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            mEglDisplay = EGL14.EGL_NO_DISPLAY
            throw IllegalStateException("Unable to initialize EGL14")
        }
        val rgbBits = 8
        val alphaBits = 8
        val renderType = EGL14.EGL_OPENGL_ES2_BIT
        val recordableAndroid = EGL14.EGL_TRUE
        val attribToChooseConfig = intArrayOf(
            EGL14.EGL_RED_SIZE, rgbBits,
            EGL14.EGL_GREEN_SIZE, rgbBits,
            EGL14.EGL_BLUE_SIZE, rgbBits,
            EGL14.EGL_ALPHA_SIZE, alphaBits,
            EGL14.EGL_DEPTH_SIZE, 24,
            EGL14.EGL_STENCIL_SIZE, 0,
            EGL14.EGL_RENDERABLE_TYPE, renderType,
            EGLExt.EGL_RECORDABLE_ANDROID, recordableAndroid,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT or EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        check(
            EGL14.eglChooseConfig(
                mEglDisplay, attribToChooseConfig, 0, configs, 0, configs.size,
                numConfigs, 0
            )
        ) { "Unable to find a suitable EGLConfig" }
        val config = configs[0]
        val attribToCreateContext = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION,
            2,
            EGL14.EGL_NONE
        )
        val context = EGL14.eglCreateContext(
            mEglDisplay,
            config,
            EGL14.EGL_NO_CONTEXT,
            attribToCreateContext,
            0
        )
        checkEglErrorOrThrow("eglCreateContext")
        mEglConfig = config
        mEglContext = context

        // Confirm with query.
        val values = IntArray(1)
        EGL14.eglQueryContext(
            mEglDisplay, mEglContext, EGL14.EGL_CONTEXT_CLIENT_VERSION, values,
            0
        )
        Log.d(TAG, "EGLContext created, client version " + values[0])
    }

    private fun createTempSurface() {
        mTempSurface = createPBufferSurface(mEglDisplay, mEglConfig!!, 1, 1)
    }

    private fun makeCurrent(eglSurface: EGLSurface) {
        Preconditions.checkNotNull(mEglDisplay)
        Preconditions.checkNotNull(mEglContext)
        check(
            EGL14.eglMakeCurrent(
                mEglDisplay,
                eglSurface,
                eglSurface,
                mEglContext
            )
        ) { "eglMakeCurrent failed" }
    }

    private fun createExternalTexture() {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        checkGlErrorOrThrow("glGenTextures")
        val texId = textures[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId)
        checkGlErrorOrThrow("glBindTexture $texId")
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        checkGlErrorOrThrow("glTexParameter")
        mExternalTextureId = texId
    }

    private fun getSurfaceSize(eglSurface: EGLSurface): Size {
        val width = querySurface(mEglDisplay, eglSurface, EGL14.EGL_WIDTH)
        val height = querySurface(mEglDisplay, eglSurface, EGL14.EGL_HEIGHT)
        return Size(width, height)
    }

    private fun releaseInternal() {
        if (mEglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(
                mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
            )

            // Destroy EGLSurfaces
            for (outputSurface in mOutputSurfaceMap.values) {
                if (outputSurface.eglSurface != EGL14.EGL_NO_SURFACE) {
                    if (!EGL14.eglDestroySurface(mEglDisplay, outputSurface.eglSurface)) {
                        checkEglErrorOrLog("eglDestroySurface")
                    }
                }
            }
            mOutputSurfaceMap.clear()

            // Destroy temp surface
            if (mTempSurface != EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroySurface(mEglDisplay, mTempSurface)
                mTempSurface = EGL14.EGL_NO_SURFACE
            }

            // Destroy EGLContext and terminate display
            if (mEglContext != EGL14.EGL_NO_CONTEXT) {
                EGL14.eglDestroyContext(mEglDisplay, mEglContext)
                mEglContext = EGL14.EGL_NO_CONTEXT
            }
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(mEglDisplay)
            mEglDisplay = EGL14.EGL_NO_DISPLAY
        }

        // Reset other members
        mEglConfig = null
        mExternalTextureId = -1
        mCurrentSurface = null
        mGlThread = null
    }

    private fun checkInitializedOrThrow(shouldInitialized: Boolean) {
        val result = shouldInitialized == mInitialized.get()
        val message =
            if (shouldInitialized) "CameraOpenGlRenderer is not initialized" else "CameraOpenGlRenderer is already initialized"
        Preconditions.checkState(result, message)
    }

    private fun checkGlThreadOrThrow() {
        Preconditions.checkState(
            mGlThread === Thread.currentThread(),
            "Method call must be called on the GL thread."
        )
    }

    private fun getOutSurfaceOrThrow(surface: Surface): OutputSurface {
        Preconditions.checkState(
            mOutputSurfaceMap.containsKey(surface),
            "The surface is not registered."
        )
        return mOutputSurfaceMap[surface]!!
    }

    private fun createOutputSurfaceInternal(surface: Surface): OutputSurface? {
        val eglSurface: EGLSurface = try {
            createWindowSurface(mEglDisplay, mEglConfig!!, surface)
        } catch (e: IllegalStateException) {
            Logger.w(TAG, "Failed to create EGL surface: " + e.message, e)
            return null
        } catch (e: IllegalArgumentException) {
            Logger.w(TAG, "Failed to create EGL surface: " + e.message, e)
            return null
        }
        val size = getSurfaceSize(eglSurface)
        return OutputSurface(eglSurface, size.width, size.height)
    }

    private fun removeOutputSurfaceInternal(surface: Surface, unregister: Boolean) {
        // Unmake current surface.
        if (mCurrentSurface === surface) {
            mCurrentSurface = null
            makeCurrent(mTempSurface)
        }

        // Remove cached EGL surface.
        val removedOutputSurface: OutputSurface? = if (unregister) {
            mOutputSurfaceMap.remove(surface)
        } else {
            mOutputSurfaceMap.put(surface, NO_OUTPUT_SURFACE)
        }

        // Destroy EGL surface.
        if (removedOutputSurface != null && removedOutputSurface !== NO_OUTPUT_SURFACE) {
            try {
                EGL14.eglDestroySurface(mEglDisplay, removedOutputSurface.eglSurface)
            } catch (e: RuntimeException) {
                Logger.w(TAG, "Failed to destroy EGL surface: " + e.message, e)
            }
        }
    }

    companion object {
        private const val TAG = "CameraOpenGlRenderer"

        private val NO_OUTPUT_SURFACE = OutputSurface(EGL14.EGL_NO_SURFACE, 0, 0)

        private fun createPBufferSurface(
            eglDisplay: EGLDisplay,
            eglConfig: EGLConfig,
            width: Int,
            height: Int,
        ): EGLSurface {
            val surfaceAttrib = intArrayOf(
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
            )
            val eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttrib, 0)
            checkEglErrorOrThrow("eglCreatePbufferSurface")
            checkNotNull(eglSurface) { "surface was null" }
            return eglSurface
        }

        private fun createWindowSurface(
            eglDisplay: EGLDisplay,
            eglConfig: EGLConfig, surface: Surface,
        ): EGLSurface {
            // Create a window surface, and attach it to the Surface we received.
            val surfaceAttrib = intArrayOf(
                EGL14.EGL_NONE
            )
            val eglSurface = EGL14.eglCreateWindowSurface(
                eglDisplay, eglConfig, surface,
                surfaceAttrib, 0
            )
            checkEglErrorOrThrow("eglCreateWindowSurface")
            checkNotNull(eglSurface) { "surface was null" }
            return eglSurface
        }

        private fun querySurface(
            eglDisplay: EGLDisplay, eglSurface: EGLSurface,
            what: Int,
        ): Int {
            val value = IntArray(1)
            EGL14.eglQuerySurface(eglDisplay, eglSurface, what, value, 0)
            return value[0]
        }

        private fun checkEglErrorOrThrow(op: String) {
            val error = EGL14.eglGetError()
            if (error != EGL14.EGL_SUCCESS) {
                throw IllegalStateException(op + ": EGL error: 0x" + Integer.toHexString(error))
            }
        }

        private fun checkEglErrorOrLog(op: String) {
            try {
                checkEglErrorOrThrow(op)
            } catch (e: IllegalStateException) {
                Logger.e(TAG, e.toString(), e)
            }
        }

        private fun checkGlErrorOrThrow(op: String) {
            val error = GLES20.glGetError()
            if (error != GLES20.GL_NO_ERROR) {
                throw IllegalStateException(op + ": GL error 0x" + Integer.toHexString(error))
            }
        }
    }

    data class OutputSurface(
        val eglSurface: EGLSurface,
        val width: Int,
        val height: Int,
    )
}