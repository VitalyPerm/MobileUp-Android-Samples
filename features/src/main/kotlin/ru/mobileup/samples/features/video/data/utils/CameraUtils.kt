package ru.mobileup.samples.features.video.data.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import java.util.concurrent.Executor

@SuppressLint("MissingPermission")
fun Context.startRecording(
    videoCapture: VideoCapture<Recorder>,
    executor: Executor = ContextCompat.getMainExecutor(this),
    consumer: Consumer<VideoRecordEvent>,
    limitTimeMillis: Long = 0,
    withAudio: Boolean,
) = videoCapture
    .output
    .prepareRecording(
        this,
        getOutputOptionForIntermediateVideo(limitTimeMillis)
    )
    .apply {
        if (withAudio) {
            withAudioEnabled()
        }
    }
    .start(executor, consumer)

fun CameraSelector.name() = when (this) {
    CameraSelector.DEFAULT_FRONT_CAMERA -> "FRONT"
    else -> "BACK"
}

fun Quality.name() = when (this) {
    Quality.SD -> "SD"
    Quality.HD -> "HD"
    Quality.FHD -> "FHD"
    Quality.UHD -> "UHD"
    else -> "NONE"
}