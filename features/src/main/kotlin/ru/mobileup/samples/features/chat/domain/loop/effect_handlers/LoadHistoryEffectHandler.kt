package ru.mobileup.samples.features.chat.domain.loop.effect_handlers

import me.aartikov.sesame.loop.EffectHandler
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ChatEffect
import ru.mobileup.samples.features.chat.domain.loop.HistoryLoadingAction
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import kotlin.coroutines.cancellation.CancellationException

class LoadHistoryEffectHandler(
    private val chatTag: ChatTag,
    private val chatRepository: ChatRepository,
    private val cachedFileStorage: CachedFileStorage
) : EffectHandler<ChatEffect, ChatAction> {

    override suspend fun handleEffect(effect: ChatEffect, actionConsumer: (ChatAction) -> Unit) {
        when (effect) {
            is ChatEffect.LoadHistory -> loadHistory(actionConsumer)

            is ChatEffect.TransformLoadedHistory -> transformLoadedHistory(
                effect.loadedMessages,
                effect.currentMessages,
                actionConsumer
            )
            else -> {
                // Do nothing
            }
        }
    }

    private suspend fun loadHistory(actionConsumer: (ChatAction) -> Unit) {
        try {
            actionConsumer(HistoryLoadingAction.HistoryLoadingStarted)
            val messages = chatRepository.getHistory(chatTag)
            actionConsumer(HistoryLoadingAction.HistoryTransforming(messages))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            actionConsumer(HistoryLoadingAction.HistoryLoadingFailed(e))
        }
    }

    private suspend fun transformLoadedHistory(
        loadedMessages: List<ChatMessage>,
        currentMessages: List<ChatMessage>,
        actionConsumer: (ChatAction) -> Unit
    ) {
        try {
            val transformedMessages = loadedMessages.map { message ->
                if (message.attachment == null) {
                    message
                } else {
                    val currentMessage = currentMessages.firstOrNull { it.id == message.id }

                    val newMessage = message.copy(
                        attachment = if (currentMessage?.attachment?.localFilePath != null) {
                            currentMessage.attachment
                        } else {
                            getAttachmentFromCachedFile(message)
                        }
                    )
                    newMessage
                }
            }
            actionConsumer(HistoryLoadingAction.HistoryLoaded(transformedMessages))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            actionConsumer(HistoryLoadingAction.HistoryLoadingFailed(e))
        }
    }

    private fun getDownloadingStatus(cachedFile: CachedFile?): ChatAttachment.DownloadingStatus {
        return when {
            cachedFile == null -> ChatAttachment.DownloadingStatus.NotDownloaded
            cachedFile.downloaded -> ChatAttachment.DownloadingStatus.Downloaded
            else -> ChatAttachment.DownloadingStatus.NotDownloaded
        }
    }

    private suspend fun getAttachmentFromCachedFile(message: ChatMessage): ChatAttachment? {
        val cachedFile = cachedFileStorage.getCachedFile(
            id = message.id.value
        )
        return message.attachment?.copy(
            localFilePath = cachedFile?.absolutePath,
            downloadingStatus = getDownloadingStatus(cachedFile)
        )
    }
}