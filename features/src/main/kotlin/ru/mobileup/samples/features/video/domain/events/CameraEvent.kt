package ru.mobileup.samples.features.video.domain.events

import android.net.Uri

sealed interface CameraEvent {
    data object StartRecord : CameraEvent
    data class ProgressRecord(val recordDuration: Long) : CameraEvent
    data class StopRecord(val recordingResult: RecordingResult) : CameraEvent
}

sealed interface RecordingResult {
    data class Success(val uri: Uri) : RecordingResult
    data class Error(val error: RecordingError) : RecordingResult
}

sealed interface RecordingError {
    data object Another : RecordingError
}