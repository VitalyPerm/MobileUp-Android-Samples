package ru.mobileup.samples.features.chat.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.data.FileHelper
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import java.time.LocalDateTime

class ChatClientFactoryImpl(
    private val chatRepository: ChatRepository,
    private val cachedFileStorage: CachedFileStorage,
    private val fileHelper: FileHelper
) : ChatClientFactory {
    private val clientMap = mutableMapOf<ChatTag, ChatClient>()

    override fun getOrCreateChatClient(chatTag: ChatTag): ChatClient {
        clientMap[chatTag]?.let { return it }

        val client = ChatClient(chatTag, chatRepository, cachedFileStorage, fileHelper)
        clientMap[chatTag] = client

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            // Clean up chat cache
            cachedFileStorage.checkAndClearIfNeedCachedFiles(
                LocalDateTime.now().minusMinutes(10)
            )
        }

        client.initialize()
        return client
    }

    override fun disposeAllChatClients() {
        for (client in clientMap.values) {
            client.dispose()
        }
        clientMap.clear()
    }
}