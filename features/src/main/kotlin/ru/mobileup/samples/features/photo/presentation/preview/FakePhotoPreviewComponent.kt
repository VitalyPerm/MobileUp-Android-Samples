package ru.mobileup.samples.features.photo.presentation.preview

import android.net.Uri
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.core.dialog.standard.fakeStandardDialogControl

class FakePhotoPreviewComponent : PhotoPreviewComponent {
    override val media: Uri = Uri.EMPTY
    override val saveDialog: StandardDialogControl = fakeStandardDialogControl()
    override fun onSaveClick() = Unit
}