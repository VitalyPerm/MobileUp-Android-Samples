package ru.mobileup.samples.features.video.domain.states

import android.net.Uri

sealed interface RenderState {
    data class Success(val uri: Uri) : RenderState
    data object WasCanceled : RenderState
    data class Error(val exception: Throwable) : RenderState
    data class InProgress(val value: Float) : RenderState
}