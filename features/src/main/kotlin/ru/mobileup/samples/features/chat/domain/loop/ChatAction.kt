package ru.mobileup.samples.features.chat.domain.loop

import android.net.Uri
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId

sealed interface ChatAction

sealed interface LifecycleAction : ChatAction {
    data object Initialize : LifecycleAction
    data object Activate : LifecycleAction
    data object Deactivate : LifecycleAction
}

sealed interface UserAction : ChatAction {
    data object ReloadHistory : UserAction
    data class SendText(val text: String) : UserAction
    data class AttachFile(val uri: Uri) : UserAction
    data class MessageClick(val messageId: ChatMessageId) : UserAction
    data class ResendPendingMessage(val messageId: ChatMessageId) : UserAction
    data class RemovePendingMessage(val messageId: ChatMessageId) : UserAction
    data class ScrolledToNotDownloadedImage(val messageId: ChatMessageId) : UserAction
}

sealed interface ConnectionAction : ChatAction {
    data object Connected : ConnectionAction
    data object Disconnected : ConnectionAction
}

sealed interface MessageReceivingAction : ChatAction {
    data class MessageReceived(val message: ChatMessage) : MessageReceivingAction
}

sealed interface MessageSendingAction : ChatAction {
    data class MessageSent(
        val pendingMessageId: ChatMessageId,
        val newMessage: ChatMessage
    ) : MessageSendingAction

    data class MessageSendingFailed(
        val pendingMessageId: ChatMessageId,
        val exception: Exception
    ) : MessageSendingAction

    data class AttachmentSendingCancelled(
        val pendingMessageId: ChatMessageId
    ) : MessageSendingAction
}

sealed interface AttachmentDownloadingAction : ChatAction {
    data class AttachmentDownloadingStarted(
        val messageId: ChatMessageId,
        val attachment: ChatAttachment
    ) : AttachmentDownloadingAction

    data class AttachmentDownloaded(
        val messageId: ChatMessageId,
        val attachment: ChatAttachment
    ) : AttachmentDownloadingAction

    data class AttachmentDownloadingFailed(
        val messageId: ChatMessageId,
        val exception: Exception
    ) : AttachmentDownloadingAction

    data class AttachmentDownloadingCancelled(val messageId: ChatMessageId) :
        AttachmentDownloadingAction
}

sealed interface MessagePreparingAction : ChatAction {
    data class OutgoingMessagePrepared(val message: ChatMessage) : MessagePreparingAction
    data class OutgoingMessagePreparingFailed(val exception: Exception) : MessagePreparingAction
}

sealed interface HistoryLoadingAction : ChatAction {
    data object HistoryLoadingStarted : HistoryLoadingAction

    // отображаем только количество сообщений, иначе логи сильно замусориваются
    data class HistoryLoaded(val messages: List<ChatMessage>) : HistoryLoadingAction {
        override fun toString(): String {
            return "HistoryLoaded(messagesCount = ${messages.size})"
        }
    }

    data class HistoryTransforming(val messages: List<ChatMessage>) : HistoryLoadingAction {
        override fun toString(): String {
            return "HistoryLoaded(messagesCount = ${messages.size})"
        }
    }

    data class HistoryLoadingFailed(val exception: Exception) : HistoryLoadingAction
}