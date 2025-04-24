package ru.mobileup.samples.features.chat.data

import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import ru.mobileup.samples.core.language.LanguageService
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.message.MessageAuthor
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

private const val NETWORK_OPERATION_FAKE_DELAY = 1000L

class FakeChatRepository(
    private val fileHelper: FileHelper,
    private val languageService: LanguageService
) : ChatRepository {

    private val fakeMessageList = when (languageService.getLanguage()) {
        AppLanguage.EN -> {
            listOf(
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
        }

        AppLanguage.RU -> {
            listOf(
                "Привет!",
                "Пока!",
                "Приятно познакомиться!",
                "Это фейковое сообщение",
                "Как прошел твой день?",
                "Я простой бот",
                "Можешь написать что хочешь!",
                "Я не ИИ!",
                "Ты тестируешь шат?",
                "Хехе ><",
                "(* ^ ω ^)"
            )
        }
    }

    private val fakeAttachmentLinkList = listOf(
        "https://cs10.pikabu.ru/post_img/2019/12/28/7/1577531889179623112.jpg",
        "https://cs9.pikabu.ru/post_img/2019/12/28/7/157753254512846777.jpg",
        "https://cs9.pikabu.ru/post_img/2019/12/28/7/1577533280132850978.jpg",
        "https://cs9.pikabu.ru/post_img/2019/12/28/7/1577532813122134198.jpg"
    )

    private val historyList = mutableListOf(
        buildFakeMessage(
            text = when (languageService.getLanguage()) {
                AppLanguage.EN -> "Message one week ago"
                AppLanguage.RU -> "Сообщение на прошлой неделе"
            },
            time = LocalDateTime.now().minusDays(7).plusMinutes(23)
        ),

        buildFakeMessage(
            text = when (languageService.getLanguage()) {
                AppLanguage.EN -> "Yesterday's message"
                AppLanguage.RU -> "Вчерашнее сообщение"
            },
            time = LocalDateTime.now().minusDays(1).plusMinutes(155)
        ),
        buildFakeMessage(
            text = when (languageService.getLanguage()) {
                AppLanguage.EN -> "Welcome to the Test chat!"
                AppLanguage.RU -> "Добро пожаловать в чат для тестирования!"
            }
        ),
        buildFakeMessage(
            text = when (languageService.getLanguage()) {
                AppLanguage.EN -> "Send a message and you will get my response :)\nTo test the error case, send a message \"error\""
                AppLanguage.RU -> "Отправь мне сообщение и получишь мой ответ :)\nДля проверки кейса с ошибкой отправь сообщение \"error\""
            }
        )
    )

    private val httpClient = HttpClient()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val messageFlow = MutableSharedFlow<ChatMessage>()

    override val isConnected: StateFlow<Boolean> = MutableStateFlow(true)

    override suspend fun getHistory(chatTag: ChatTag): List<ChatMessage> {
        delay(NETWORK_OPERATION_FAKE_DELAY)
        return historyList
    }

    override fun getMessagesFlow(chatTag: ChatTag): Flow<ChatMessage> {
        return messageFlow
    }

    override suspend fun sendTextMessage(chatTag: ChatTag, text: String): ChatMessage {
        delay(NETWORK_OPERATION_FAKE_DELAY)
        if (text.lowercase() == "error") throw Exception("Fake error")
        receiveTextFakeMessageWithDelay()

        val resultMessage = ChatMessage(
            id = generateGlobalId(),
            author = MessageAuthor.Me,
            text = text,
            time = LocalDateTime.now(),
            attachment = null,
            messageStatus = MessageStatus.Sent
        )
        historyList.add(resultMessage)
        return resultMessage
    }

    override suspend fun sendAttachmentMessage(
        chatTag: ChatTag,
        attachment: ChatAttachment
    ): ChatMessage {
        delay(NETWORK_OPERATION_FAKE_DELAY)
        receiveAttachmentFakeMessageWithDelay()

        val resultMessage = ChatMessage(
            id = generateGlobalId(),
            author = MessageAuthor.Me,
            text = "",
            time = LocalDateTime.now(),
            attachment = attachment,
            messageStatus = MessageStatus.Sent
        )
        historyList.add(resultMessage)
        return resultMessage
    }

    override suspend fun downloadAttachment(url: String, filename: String) {
        withContext(Dispatchers.IO) {
            httpClient.prepareGet(url).execute {
                fileHelper.downloadCachedFile(it.body(), filename)
            }
        }
    }

    private fun receiveTextFakeMessageWithDelay() {
        scope.launch {
            delay(NETWORK_OPERATION_FAKE_DELAY)
            val message = buildFakeMessage(
                text = fakeMessageList[Random.nextInt(fakeMessageList.size)]
            )
            messageFlow.emit(message)
            historyList.add(message)
        }
    }

    private fun receiveAttachmentFakeMessageWithDelay() {
        scope.launch {
            delay(NETWORK_OPERATION_FAKE_DELAY)
            val message = buildFakeMessage(
                text = "",
                attachment = ChatAttachment(
                    remoteLink = fakeAttachmentLinkList[Random.nextInt(fakeAttachmentLinkList.size)],
                    filename = UUID.randomUUID().toString(),
                    extension = ".jpg",
                    type = ChatAttachment.Type.IMAGE,
                    downloadingStatus = ChatAttachment.DownloadingStatus.NotDownloaded
                )
            )
            messageFlow.emit(message)
            historyList.add(message)
        }
    }

    private fun generateGlobalId() = ChatMessageId("global_${UUID.randomUUID()}")

    private fun buildFakeMessage(
        text: String,
        attachment: ChatAttachment? = null,
        time: LocalDateTime = LocalDateTime.now()
    ) = ChatMessage(
        id = generateGlobalId(),
        author = MessageAuthor.User(
            title = StringDesc.Raw("Fake User")
        ),
        text = text,
        time = time,
        attachment = attachment,
        messageStatus = MessageStatus.Sent
    )
}