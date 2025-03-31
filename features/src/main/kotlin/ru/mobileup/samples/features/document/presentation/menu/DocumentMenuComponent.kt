package ru.mobileup.samples.features.document.presentation.menu

import android.net.Uri

interface DocumentMenuComponent {

    fun onPreviewClick(mediaUri: Uri?)

    sealed interface Output {
        data class PreviewRequested(val mediaUri: Uri) : Output
    }
}