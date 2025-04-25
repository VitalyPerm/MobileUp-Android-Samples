package ru.mobileup.samples.features.chat.domain.loop.effect_handlers

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loop.EffectHandler
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ChatEffect
import ru.mobileup.samples.features.chat.domain.loop.MessageSendingAction
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.ChatTag
import kotlin.coroutines.cancellation.CancellationException

class SendMessageEffectHandler(
    private val chatTag: ChatTag,
    private val chatRepository: ChatRepository,
    private val cachedFileStorage: CachedFileStorage
) : EffectHandler<ChatEffect, ChatAction> {
    private val sendingMessageMap = mutableMapOf<ChatMessageId, Job>()

    override suspend fun handleEffect(effect: ChatEffect, actionConsumer: (ChatAction) -> Unit) {
        when (effect) {
            is ChatEffect.SendMessage -> {
                sendMessage(effect.message, actionConsumer)
            }

            is ChatEffect.CancelSendingAttachment -> {
                cancelUploadingAttachment(effect.messageId, actionConsumer)
            }

            else -> {
                // Do nothing
            }
        }
    }

    private suspend fun sendMessage(message: ChatMessage, actionConsumer: (ChatAction) -> Unit) {
        try {
            coroutineScope {
                val sendingMessageJob = launch(start = CoroutineStart.LAZY) {
                    val newMessage = if (message.attachment == null) {
                        chatRepository.sendTextMessage(chatTag, message.text)
                    } else {
                        val updatedMessage =
                            chatRepository.sendAttachmentMessage(chatTag, message.attachment)
                        cachedFileStorage.updateUploadingStatusAndId(
                            oldId = message.id.value,
                            newId = updatedMessage.id.value,
                            uploaded = true
                        )
                        val cachedFile = cachedFileStorage.getCachedFile(
                            id = updatedMessage.id.value
                        )
                        updatedMessage.copy(
                            attachment = updatedMessage.attachment?.copy(
                                localFilePath = cachedFile?.absolutePath,
                                downloadingStatus = getDownloadingStatus(cachedFile)
                            )
                        )
                    }
                    actionConsumer(
                        MessageSendingAction.MessageSent(
                            pendingMessageId = message.id,
                            newMessage = newMessage
                        )
                    )
                    sendingMessageMap.remove(message.id)
                }
                sendingMessageMap[message.id] = sendingMessageJob
                sendingMessageJob.start()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            actionConsumer(
                MessageSendingAction.MessageSendingFailed(
                    pendingMessageId = message.id,
                    exception = e
                )
            )
            sendingMessageMap.remove(message.id)
        }
    }

    private suspend fun cancelUploadingAttachment(
        messageId: ChatMessageId,
        actionConsumer: (ChatAction) -> Unit
    ) {
        try {
            sendingMessageMap[messageId]?.cancel()
            cachedFileStorage.clearCachedFileById(messageId.value)
            actionConsumer(
                MessageSendingAction.AttachmentSendingCancelled(
                    pendingMessageId = messageId
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            actionConsumer(
                MessageSendingAction.MessageSendingFailed(
                    pendingMessageId = messageId,
                    exception = e
                )
            )
        } finally {
            sendingMessageMap.remove(messageId)
        }
    }

    private fun getDownloadingStatus(cachedFile: CachedFile?): ChatAttachment.DownloadingStatus {
        return when {
            cachedFile == null -> ChatAttachment.DownloadingStatus.NotDownloaded
            cachedFile.downloaded -> ChatAttachment.DownloadingStatus.Downloaded
            else -> ChatAttachment.DownloadingStatus.NotDownloaded
        }
    }
}