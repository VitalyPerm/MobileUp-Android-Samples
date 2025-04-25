package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.options.KeyboardOptions
import ru.mobileup.kmm_form_validation.options.KeyboardType
import ru.mobileup.samples.core.dialog.standard.DialogButton
import ru.mobileup.samples.core.dialog.standard.StandardDialogData
import ru.mobileup.samples.core.dialog.standard.standardDialogControl
import ru.mobileup.samples.core.external_apps.data.ExternalAppService
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.utils.InputControl
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.chat.domain.ChatClientFactory
import ru.mobileup.samples.features.chat.domain.exception.isFileCopingException
import ru.mobileup.samples.features.chat.domain.exception.isTooBigFileSizeException
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId

private const val INPUT_MAX_LENGTH = 400

class RealChatComponent(
    componentContext: ComponentContext,
    chatClientFactory: ChatClientFactory,
    private val externalAppService: ExternalAppService,
    private val messageService: MessageService
) : ComponentContext by componentContext, ChatComponent {

    private val chatClient = chatClientFactory.getOrCreateChatClient(
        ChatTag("Test Chat")
    )

    override val chatState = chatClient.stateFlow

    override val scrollToEnd = MutableSharedFlow<Unit>()

    override val inputControl = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        textTransformation = { text -> text },
        maxLength = INPUT_MAX_LENGTH
    )

    override val messageSendFailedDialog = standardDialogControl("sendFailedDialog")

    init {
        lifecycle.subscribe(
            object : Lifecycle.Callbacks {
                override fun onResume() {
                    super.onResume()
                    chatClient.activate()
                }

                override fun onPause() {
                    super.onPause()
                    chatClient.deactivate()
                }
            }
        )

        componentScope.launch {
            chatClient.effectFlow.collect {
                when (it) {
                    is ExternalChatEffect.ReactToAddedMessages -> {
                        scrollToEnd.emit(Unit)
                    }

                    is ExternalChatEffect.ShowFailedMessageDialog -> {
                        showMessageSendFailedDialog(it.messageId)
                    }

                    is ExternalChatEffect.ShowError -> {
                        showErrorMessage(it.exception)
                    }

                    is ExternalChatEffect.OpenAttachmentFile -> {
                        externalAppService.openFile(
                            filePath = it.filePath
                        )
                    }
                }
            }
        }
    }

    override fun onReloadClick() {
        chatClient.reloadHistory()
    }

    override fun onSendMessage() {
        chatClient.sendText(inputControl.value.value)
        inputControl.onValueChange("")
    }

    override fun onSendFile(uri: Uri) {
        chatClient.attachFile(uri)
    }

    override fun onMessageClick(messageId: ChatMessageId) {
        chatClient.handleMessageClick(messageId)
    }

    private fun showMessageSendFailedDialog(messageId: ChatMessageId) {
        messageSendFailedDialog.show(
            StandardDialogData(
                title = R.string.chat_send_failed_dialog_title.strResDesc(),
                message = R.string.chat_send_failed_dialog_message.strResDesc(),
                confirmButton = DialogButton(
                    text = R.string.chat_send_failed_dialog_resend.strResDesc(),
                    action = {
                        chatClient.resendPendingMessage(messageId)
                    }
                ),
                dismissButton = DialogButton(
                    text = R.string.chat_send_failed_dialog_remove.strResDesc(),
                    action = {
                        chatClient.removePendingMessage(messageId)
                    }
                )
            )
        )
    }

    private fun showErrorMessage(exception: Exception) {
        messageService.showMessage(
            Message(
                text = StringDesc.Resource(
                    when {
                        exception.isFileCopingException() -> R.string.chat_error_file_download
                        exception.isTooBigFileSizeException() -> R.string.chat_error_file_too_big
                        else -> R.string.chat_error_unknown
                    }
                )
            )
        )
    }
}