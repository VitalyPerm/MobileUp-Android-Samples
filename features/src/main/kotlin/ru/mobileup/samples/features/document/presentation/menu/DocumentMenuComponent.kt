package ru.mobileup.samples.features.document.presentation.menu

import android.net.Uri

interface DocumentMenuComponent {

    fun onPreviewClick(media: Uri?)

    sealed interface Output {
        data class PreviewRequested(val media: Uri) : Output
    }
}