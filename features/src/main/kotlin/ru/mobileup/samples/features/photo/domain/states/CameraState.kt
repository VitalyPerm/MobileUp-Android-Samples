package ru.mobileup.samples.features.photo.domain.states

import androidx.camera.core.CameraSelector

data class CameraState(
    val cameraSelector: CameraSelector,
    val torchState: Boolean
) {
    companion object {
        fun build() = CameraState(
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            torchState = false
        )
    }
}