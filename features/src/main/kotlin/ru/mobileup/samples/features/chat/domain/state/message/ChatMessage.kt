package ru.mobileup.samples.features.chat.domain.state.message

import java.time.LocalDateTime
import java.util.UUID

@JvmInline
value class ChatMessageId(val value: String) {
    companion object {
        fun generateLocalId() = ChatMessageId("local_${UUID.randomUUID()}")
    }
}

data class ChatMessage(
    val id: ChatMessageId,
    val author: MessageAuthor,
    val text: String,
    val time: LocalDateTime,
    val attachment: ChatAttachment? = null,
    val messageStatus: MessageStatus
)