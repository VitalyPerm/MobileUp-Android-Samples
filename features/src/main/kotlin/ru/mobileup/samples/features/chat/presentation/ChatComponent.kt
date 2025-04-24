package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.features.chat.domain.state.ChatState
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId

interface ChatComponent {

    val chatState: StateFlow<ChatState>

    val scrollToEnd: SharedFlow<Unit>

    val inputControl: InputControl

    val messageSendFailedDialog: StandardDialogControl

    fun onReloadClick()

    fun onSendMessage()

    fun onSendFile(uri: Uri)

    fun onMessageClick(messageId: ChatMessageId)
}