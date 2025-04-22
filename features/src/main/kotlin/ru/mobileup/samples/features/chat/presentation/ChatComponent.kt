package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.state.ChatState

interface ChatComponent {

    val inputControl: InputControl

    val chatState: StateFlow<ChatState>

    val effectFlow: SharedFlow<ExternalChatEffect>

    fun onSendMessage()

    fun onSendFile(uri: Uri)
}