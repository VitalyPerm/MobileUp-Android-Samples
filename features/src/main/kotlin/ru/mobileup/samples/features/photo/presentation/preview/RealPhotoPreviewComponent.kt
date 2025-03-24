package ru.mobileup.samples.features.photo.presentation.preview

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.dialog.standard.DialogButton
import ru.mobileup.samples.core.dialog.standard.StandardDialogData
import ru.mobileup.samples.core.dialog.standard.standardDialogControl
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.photo.data.PhotoFileManager

class RealPhotoPreviewComponent(
    override val media: Uri,
    componentContext: ComponentContext,
    private val photoFileManager: PhotoFileManager,
    private val messageService: MessageService
) : ComponentContext by componentContext, PhotoPreviewComponent {

    override val saveDialog = standardDialogControl("saveDialog")

    override fun onSaveClick() = saveDialog.show(
        StandardDialogData(
            title = R.string.save_photo_dialog_title.strResDesc(),
            message = R.string.save_photo_dialog_message.strResDesc(),
            confirmButton = DialogButton(
                text = R.string.photo_confirm_btn.strResDesc(),
                action = ::savePhoto
            ),
            dismissButton = DialogButton(
                text = R.string.photo_dismiss_btn.strResDesc(),
                action = saveDialog::dismiss
            )
        )
    )

    private fun savePhoto() {
        componentScope.launch {
            photoFileManager.movePhotoToMediaStore(media)

            messageService.showMessage(
                Message(
                    text = StringDesc.Resource(
                        R.string.photo_saved
                    )
                )
            )
        }
    }
}