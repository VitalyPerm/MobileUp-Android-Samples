package ru.mobileup.samples.features.chat

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.chat.data.ChatDatabase
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.data.FakeChatRepository
import ru.mobileup.samples.features.chat.data.FileHelper
import ru.mobileup.samples.features.chat.data.FileHelperImpl
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.data.cached_file.RoomCachedFileStorage
import ru.mobileup.samples.features.chat.domain.ChatClient
import ru.mobileup.samples.features.chat.domain.ChatClientImpl
import ru.mobileup.samples.features.chat.presentation.ChatComponent
import ru.mobileup.samples.features.chat.presentation.RealChatComponent

val chatModule = module {
    // Database
    single { ChatDatabase.create(get()) }

    single { get<ChatDatabase>().getCachedFileDao() }
    single<CachedFileStorage> { RoomCachedFileStorage(get(), get()) }
    single<FileHelper> { FileHelperImpl(get()) }
    single<ChatRepository> { FakeChatRepository(get(), get()) }
    single<ChatClient> { ChatClientImpl(get(), get(), get()) }
}

fun ComponentFactory.createChatComponent(componentContext: ComponentContext): ChatComponent {
    return RealChatComponent(componentContext, get(), get())
}