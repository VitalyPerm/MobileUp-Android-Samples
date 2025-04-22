package ru.mobileup.samples.features.chat.data

import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import ru.mobileup.samples.features.chat.domain.state.message.MessageAuthor
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus
import java.time.LocalDateTime
import kotlin.random.Random

private const val MESSAGE_DELAY = 1500L

class FakeChatRepository : ChatRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val messageFlow = MutableSharedFlow<ChatMessage>()

    override val isConnected: StateFlow<Boolean> = MutableStateFlow(true)

    override suspend fun getHistory(chatTag: ChatTag): List<ChatMessage> {
        return listOf(
            buildFakeMessage(
                text = "Welcome to the Test chat!"
            ),
            buildFakeMessage(
                text = "Send a message and you will get my response :)"
            )
        )
    }

    override fun getMessagesFlow(chatTag: ChatTag): Flow<ChatMessage> {
        return messageFlow
    }

    override suspend fun sendTextMessage(chatTag: ChatTag, text: String): ChatMessage {
        delay(MESSAGE_DELAY)
        receiveFakeMessageWithDelay()
        return ChatMessage(
            id = ChatMessageId.generateLocalId(),
            author = MessageAuthor.Me,
            text = text,
            time = LocalDateTime.now(),
            attachment = null,
            messageStatus = MessageStatus.Sent
        )
    }

    override suspend fun sendAttachmentMessage(
        chatTag: ChatTag,
        attachment: ChatAttachment
    ): ChatMessage {
        return ChatMessage(
            id = ChatMessageId.generateLocalId(),
            author = MessageAuthor.Me,
            text = "",
            time = LocalDateTime.now(),
            attachment = attachment,
            messageStatus = MessageStatus.Sent
        )
    }

    override suspend fun downloadAttachment(url: String, filename: String) {
        // Download
    }

    private val fakeMessageList = listOf(
        "Hello!",
        "Bye!",
        "Nice to meet you!",
        "It's a fake message",
        "How was your day?",
        "I'm a simple bot",
        "You can write anything!",
        "I'm not an AI!",
        "Are you testing the chat?",
        "Hehe ><",
        "(* ^ ω ^)"
    )

    private fun receiveFakeMessageWithDelay() {
        scope.launch {
            delay(MESSAGE_DELAY)
            messageFlow.emit(
                buildFakeMessage(
                    text = fakeMessageList[Random.nextInt(fakeMessageList.size)]
                )
            )
        }
    }

    private fun buildFakeMessage(text: String) = ChatMessage(
        id = ChatMessageId.generateLocalId(),
        author = MessageAuthor.User(
            title = StringDesc.Raw("Fake User")
        ),
        text = text,
        time = LocalDateTime.now(),
        attachment = null,
        messageStatus = MessageStatus.Sent
    )
}