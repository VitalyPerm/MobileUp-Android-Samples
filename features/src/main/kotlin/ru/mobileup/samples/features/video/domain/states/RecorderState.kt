package ru.mobileup.samples.features.video.domain.states

import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality

data class RecorderState(
    val cameraSelector: CameraSelector,
    val fps: Int,
    val quality: Quality,
    val torchState: Boolean,
    val isRecording: Boolean,
    val durationMs: Long
) {
    companion object {
        val availableCameraSelectorList = listOf(
            CameraSelector.DEFAULT_BACK_CAMERA,
            CameraSelector.DEFAULT_FRONT_CAMERA
        )

        val availableFpsList = listOf(30, 60)

        val availableQualityList = listOf(Quality.SD, Quality.HD, Quality.FHD, Quality.UHD)

        val availableTorchStateList = listOf(false, true)

        fun build() = RecorderState(
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            fps = 60,
            quality = Quality.UHD,
            torchState = false,
            isRecording = false,
            durationMs = 0L
        )
    }
}