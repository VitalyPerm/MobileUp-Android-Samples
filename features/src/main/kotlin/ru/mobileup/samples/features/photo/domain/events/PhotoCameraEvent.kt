package ru.mobileup.samples.features.photo.domain.events

import android.net.Uri

sealed interface PhotoCameraEvent {
    data class PhotoCaptured(val uri: Uri) : PhotoCameraEvent
    data class Error(val error: PhotoError) : PhotoCameraEvent
}

sealed interface PhotoError {
    data object Another : PhotoError
}