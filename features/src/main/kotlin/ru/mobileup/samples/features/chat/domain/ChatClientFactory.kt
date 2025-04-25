package ru.mobileup.samples.features.chat.domain

import ru.mobileup.samples.features.chat.domain.state.ChatTag

interface ChatClientFactory {
    fun getOrCreateChatClient(chatTag: ChatTag): ChatClient
    fun disposeAllChatClients()
}