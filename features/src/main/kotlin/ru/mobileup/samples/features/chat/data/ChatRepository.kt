package ru.mobileup.samples.features.chat.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.ChatTag

interface ChatRepository {

    val isConnected: StateFlow<Boolean>

    suspend fun getHistory(chatTag: ChatTag): List<ChatMessage>

    fun getMessagesFlow(chatTag: ChatTag): Flow<ChatMessage>

    suspend fun sendTextMessage(chatTag: ChatTag, text: String): ChatMessage

    suspend fun sendAttachmentMessage(chatTag: ChatTag, attachment: ChatAttachment): ChatMessage

    suspend fun downloadAttachment(url: String, filename: String)
}