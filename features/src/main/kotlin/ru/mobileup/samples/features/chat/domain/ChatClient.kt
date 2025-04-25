package ru.mobileup.samples.features.chat.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.data.FileHelper
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.domain.loop.ChatLoop
import ru.mobileup.samples.features.chat.domain.loop.ChatReducer
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.loop.LifecycleAction
import ru.mobileup.samples.features.chat.domain.loop.UserAction
import ru.mobileup.samples.features.chat.domain.loop.action_sources.ChatActionSource
import ru.mobileup.samples.features.chat.domain.loop.effect_handlers.DownloadAttachmentEffectHandler
import ru.mobileup.samples.features.chat.domain.loop.effect_handlers.ExternalChatEffectHandler
import ru.mobileup.samples.features.chat.domain.loop.effect_handlers.LoadHistoryEffectHandler
import ru.mobileup.samples.features.chat.domain.loop.effect_handlers.PrepareOutgoingMessageEffectHandler
import ru.mobileup.samples.features.chat.domain.loop.effect_handlers.SendMessageEffectHandler
import ru.mobileup.samples.features.chat.domain.loop.utils.ChatLogger
import ru.mobileup.samples.features.chat.domain.state.ChatState
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId

class ChatClient(
    chatTag: ChatTag,
    chatRepository: ChatRepository,
    cachedFileStorage: CachedFileStorage,
    fileHelper: FileHelper
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _effectFlow = MutableSharedFlow<ExternalChatEffect>(extraBufferCapacity = 100)

    private val loop: ChatLoop = ChatLoop(
        initialState = ChatState.INITIAL,
        reducer = ChatReducer(),
        effectHandlers = listOf(
            PrepareOutgoingMessageEffectHandler(cachedFileStorage, fileHelper),
            LoadHistoryEffectHandler(chatTag, chatRepository, cachedFileStorage),
            SendMessageEffectHandler(chatTag, chatRepository, cachedFileStorage),
            ExternalChatEffectHandler { event ->
                scope.launch {
                    _effectFlow.emit(event)
                }
            },
            DownloadAttachmentEffectHandler(chatRepository, cachedFileStorage)
        ),
        actionSources = listOf(
            ChatActionSource(chatTag, chatRepository)
        ),
        logger = ChatLogger(chatTag)
    )

    private val loopJob: Job = scope.launch {
        loop.start()
    }

    val stateFlow: StateFlow<ChatState> get() = loop.stateFlow

    val effectFlow: SharedFlow<ExternalChatEffect> get() = _effectFlow

    fun initialize() {
        loop.dispatch(LifecycleAction.Initialize)
    }

    fun activate() {
        loop.dispatch(LifecycleAction.Activate)
    }

    fun deactivate() {
        loop.dispatch(LifecycleAction.Deactivate)
    }

    fun reloadHistory() {
        loop.dispatch(UserAction.ReloadHistory)
    }

    fun sendText(text: String) {
        loop.dispatch(UserAction.SendText(text))
    }

    fun attachFile(uri: Uri) {
        loop.dispatch(UserAction.AttachFile(uri))
    }

    fun handleMessageClick(messageId: ChatMessageId) {
        loop.dispatch(UserAction.MessageClick(messageId))
    }

    fun resendPendingMessage(messageId: ChatMessageId) {
        loop.dispatch(UserAction.ResendPendingMessage(messageId))
    }

    fun removePendingMessage(messageId: ChatMessageId) {
        loop.dispatch(UserAction.RemovePendingMessage(messageId))
    }

    // We can dispose chat after logout
    fun dispose() {
        loopJob.cancel()
    }

    fun scrolledToNotDownloadedImage(messageId: ChatMessageId) {
        loop.dispatch(UserAction.ScrolledToNotDownloadedImage(messageId))
    }
}