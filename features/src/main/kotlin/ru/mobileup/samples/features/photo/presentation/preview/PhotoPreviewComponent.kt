package ru.mobileup.samples.features.photo.presentation.preview

import android.net.Uri
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl

interface PhotoPreviewComponent {

    val media: Uri

    val saveDialog: StandardDialogControl

    fun onSaveClick()
}