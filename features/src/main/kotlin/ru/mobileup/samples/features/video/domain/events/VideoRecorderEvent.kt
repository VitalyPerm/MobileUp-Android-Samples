package ru.mobileup.samples.features.video.domain.events

import android.net.Uri

sealed interface VideoRecorderEvent {
    data object StartRecord : VideoRecorderEvent
    data class ProgressRecord(val recordDuration: Long) : VideoRecorderEvent
    data class StopRecord(val recordingResult: RecordingResult) : VideoRecorderEvent
}

sealed interface RecordingResult {
    data class Success(val uri: Uri) : RecordingResult
    data class Error(val error: RecordingError) : RecordingResult
}

sealed interface RecordingError {
    data object Another : RecordingError
}