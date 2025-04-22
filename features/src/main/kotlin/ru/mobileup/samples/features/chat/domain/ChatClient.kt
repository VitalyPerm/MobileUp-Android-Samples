package ru.mobileup.samples.features.chat.domain

import android.net.Uri
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.ChatState

interface ChatClient {

    val stateFlow: StateFlow<ChatState>

    val effectFlow: SharedFlow<ExternalChatEffect>

    fun initialize()

    fun activate()

    fun deactivate()

    fun reloadHistory()

    fun sendText(text: String)

    fun attachFile(uri: Uri)

    fun handleMessageClick(messageId: ChatMessageId)

    fun resendPendingMessage(messageId: ChatMessageId)

    fun removePendingMessage(messageId: ChatMessageId)

    fun dispose()

    fun scrolledToNotDownloadedImage(messageId: ChatMessageId)
}