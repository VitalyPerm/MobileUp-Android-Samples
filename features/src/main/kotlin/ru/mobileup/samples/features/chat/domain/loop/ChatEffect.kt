package ru.mobileup.samples.features.chat.domain.loop

import android.net.Uri
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId

sealed interface ChatEffect {
    data object LoadHistory : ChatEffect
    data class TransformLoadedHistory(
        val loadedMessages: List<ChatMessage>,
        val currentMessages: List<ChatMessage>
    ) : ChatEffect

    data class PrepareOutgoingTextMessage(val text: String) : ChatEffect
    data class SendMessage(val message: ChatMessage) : ChatEffect
    data class PrepareOutgoingAttachmentMessage(val uri: Uri) : ChatEffect
    data class CancelSendingAttachment(val messageId: ChatMessageId) : ChatEffect
    data class CancelDownloadingAttachment(val messageId: ChatMessageId) : ChatEffect
    data class DownloadAttachment(val message: ChatMessage) : ChatEffect
}

// ExternalChatEffect не обрабатываются внутренней логикой ChatClient, а выдаются наружу
sealed class ExternalChatEffect : ChatEffect {
    data class ReactToAddedMessages(val pendingMessage: Boolean) : ExternalChatEffect()
    data class ShowError(val exception: Exception) : ExternalChatEffect()
    data class ShowFailedMessageDialog(val messageId: ChatMessageId) : ExternalChatEffect()
    data class OpenAttachmentFile(val filePath: String) : ExternalChatEffect()
}