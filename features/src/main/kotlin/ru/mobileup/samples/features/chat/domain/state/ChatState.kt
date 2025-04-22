package ru.mobileup.samples.features.chat.domain.state

import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus

data class ChatState(
    val connected: Boolean,
    val active: Boolean,
    val chatMessagesState: ChatMessagesState,
    val historyLoadingState: HistoryLoadingState,
) {
    companion object {
        val INITIAL = ChatState(
            connected = false,
            active = false,
            chatMessagesState = ChatMessagesState(emptyList(), emptyList()),
            historyLoadingState = HistoryLoadingState.INITIAL
        )
    }

    fun findMessage(messageId: ChatMessageId): ChatMessage? {
        return chatMessagesState.messages.find { it.id == messageId }
            ?: chatMessagesState.pendingMessages.find { it.id == messageId }
    }

    fun findPendingMessage(messageId: ChatMessageId): ChatMessage? {
        return chatMessagesState.pendingMessages.find { it.id == messageId }
    }
}

data class ChatMessagesState(
    val messages: List<ChatMessage>,
    val pendingMessages: List<ChatMessage>
)

data class HistoryLoadingState(
    val inProgress: Boolean,
    val loaded: Boolean,
    val error: Exception?
) {
    companion object {
        val INITIAL = HistoryLoadingState(
            inProgress = false,
            loaded = false,
            error = null
        )
    }
}

fun ChatState.setActive(value: Boolean) = copy(active = value)

fun ChatState.setConnected(value: Boolean) = copy(connected = value)

fun ChatState.addMessage(message: ChatMessage) = copy(
    chatMessagesState = chatMessagesState.copy(
        messages = chatMessagesState.messages + message
    )
)

fun ChatState.addPendingMessage(message: ChatMessage) = copy(
    chatMessagesState = chatMessagesState.copy(
        pendingMessages = chatMessagesState.pendingMessages + message
    )
)

fun ChatState.changePendingMessageSendingStatus(
    messageId: ChatMessageId,
    messageStatus: MessageStatus
) = copy(
    chatMessagesState = chatMessagesState.copy(
        pendingMessages = chatMessagesState.pendingMessages.map {
            if (it.id == messageId) {
                it.copy(messageStatus = messageStatus)
            } else {
                it
            }
        }
    )
)

fun ChatState.removePendingMessage(messageId: ChatMessageId) = copy(
    chatMessagesState = chatMessagesState.copy(
        pendingMessages = chatMessagesState.pendingMessages.filter { it.id != messageId }
    )
)

fun ChatState.setHistoryLoadingInProgress(value: Boolean) = copy(
    historyLoadingState = historyLoadingState.copy(inProgress = value)
)

fun ChatState.mergeWithLoadedHistory(historyMessages: List<ChatMessage>): ChatState {
    val maxTimeInHistory = historyMessages.maxOfOrNull { it.time }

    // Мы не можем просто заменить текущие messages на historyMessages,
    // потому что пока загружалась история по сокету могли прийти еще более новые сообщения.
    val receivedDuringHistoryLoading = if (maxTimeInHistory != null) {
        chatMessagesState.messages.filter { it.time > maxTimeInHistory }
    } else {
        chatMessagesState.messages
    }

    return copy(
        chatMessagesState = chatMessagesState.copy(
            messages = historyMessages + receivedDuringHistoryLoading
        )
    )
}

fun ChatState.setHistoryLoaded() = copy(
    historyLoadingState = historyLoadingState.copy(
        inProgress = false,
        loaded = true,
        error = null
    )
)

fun ChatState.setHistoryLoadingError(error: Exception) = copy(
    historyLoadingState = historyLoadingState.copy(error = error)
)

fun ChatState.changeAttachmentMessage(
    messageId: ChatMessageId,
    attachment: ChatAttachment
) = copy(
    chatMessagesState = chatMessagesState.copy(
        messages = chatMessagesState.messages.map {
            if (it.id == messageId) {
                it.copy(
                    attachment = attachment
                )
            } else {
                it
            }
        }
    )
)

fun ChatState.changeAttachmentMessageDownloadingStatus(
    messageId: ChatMessageId,
    downloadingStatus: ChatAttachment.DownloadingStatus
) = copy(
    chatMessagesState = chatMessagesState.copy(
        messages = chatMessagesState.messages.map {
            if (it.id == messageId) {
                it.copy(
                    attachment = it.attachment?.copy(
                        downloadingStatus = downloadingStatus
                    )
                )
            } else {
                it
            }
        }
    )
)