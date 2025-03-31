package ru.mobileup.samples.features.document.presentation.preview

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.net.toUri
import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileup.samples.core.activity.ActivityProvider
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.document.data.DocumentManager
import ru.mobileup.samples.features.document.domain.DocumentMetadata

class RealDocumentPreviewComponent(
    private val mediaUri: String,
    componentContext: ComponentContext,
    documentManager: DocumentManager,
    private val activityProvider: ActivityProvider,
    private val messageService: MessageService
) : ComponentContext by componentContext, DocumentPreviewComponent {

    override val documentMetadataState = MutableStateFlow<DocumentMetadata?>(null)

    init {
        documentManager.loadMetadata(mediaUri.toUri())?.let { documentMetadata ->
            documentMetadataState.update {
                documentMetadata
            }
        } ?: run {
            messageService.showMessage(
                Message(
                    text = StringDesc.Resource(
                        R.string.document_load_failed
                    )
                )
            )
        }
    }

    override fun onOpenClick() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(mediaUri.toUri(), "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            activityProvider.activity?.startActivity(Intent.createChooser(intent, "Open PDF with"))
        } catch (e: ActivityNotFoundException) {
            messageService.showMessage(
                Message(
                    text = StringDesc.Resource(
                        R.string.document_pdf_viewer_not_found
                    )
                )
            )
        } catch (e: SecurityException) {
            messageService.showMessage(
                Message(
                    text = StringDesc.Resource(
                        R.string.document_permission_denied
                    )
                )
            )
        }
    }
}