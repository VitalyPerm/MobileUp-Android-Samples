package ru.mobileup.samples.features.photo.presentation.camera.controller

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import co.touchlab.kermit.Logger
import ru.mobileup.samples.features.photo.data.utils.getOutputFileForPhoto
import ru.mobileup.samples.features.photo.domain.events.PhotoCameraEvent
import ru.mobileup.samples.features.photo.domain.events.PhotoError
import ru.mobileup.samples.features.photo.domain.states.CameraState

private const val TAG = "PhotoCamera"

class PhotoCameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onPhotoCameraEvent: (PhotoCameraEvent) -> Unit,
    private val onCameraInitializationFailed: () -> Unit,
    private val onPlaceHolderUpdated: (Bitmap?) -> Unit
) {
    private val cameraProvider = ProcessCameraProvider.getInstance(context).get()

    private var cameraLink: Camera? = null

    private var imageCapture: ImageCapture? = null
    private var isReleased = false

    var cameraState = CameraState.build()
        set(value) {
            // Rebuild only if we changed the camera configuration
            if (field.cameraSelector != value.cameraSelector) {
                field = value
                onPlaceHolderUpdated(previewView.bitmap)
                setupImageUseCase()
            }
        }

    init {
        setupImageUseCase()
    }

    fun takePhoto() {
        val file = context.getOutputFileForPhoto()
        file.mkdirs()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
            .setMetadata(ImageCapture.Metadata().also {
                it.isReversedHorizontal =
                    CameraSelector.DEFAULT_FRONT_CAMERA == cameraState.cameraSelector
            })
            .build()

        imageCapture?.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onPhotoCameraEvent(
                        outputFileResults.savedUri?.let {
                            PhotoCameraEvent.PhotoCaptured(
                                it
                            )
                        } ?: PhotoCameraEvent.Error(PhotoError.Another)
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    onPhotoCameraEvent(
                        PhotoCameraEvent.Error(PhotoError.Another)
                    )
                }
            }
        )
    }

    private fun setupImageUseCase() {
        if (isReleased) return
        imageCapture = createImageCaptureUseCase()
    }

    private fun createImageCaptureUseCase(): ImageCapture? {
        cameraProvider.unbindAll()

        val preview = Preview.Builder()
            .build()
            .apply {
                surfaceProvider = previewView.surfaceProvider
            }

        val imageCapture = ImageCapture.Builder()
            .setFlashMode(
                if (cameraState.torchState) {
                    ImageCapture.FLASH_MODE_ON
                } else {
                    ImageCapture.FLASH_MODE_OFF
                }
            )
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) {
            onPlaceHolderUpdated(null)
            imageAnalysis.clearAnalyzer()
        }

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageCapture)
            .addUseCase(imageAnalysis)
            .build()

        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraState.cameraSelector,
                useCaseGroup = useCaseGroup
            ).also {
                cameraLink = it
            }
        } catch (e: Exception) {
            Logger.withTag(TAG).d("Camera initialization failed: $e")
            onCameraInitializationFailed()
            return null
        }

        return imageCapture
    }
}