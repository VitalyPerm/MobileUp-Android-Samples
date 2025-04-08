package ru.mobileup.samples.features.photo.domain.states

import android.net.Uri
import androidx.camera.core.CameraSelector

data class CameraState(
    val uris: List<Uri> = listOf(),
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val torchState: Boolean = false
)