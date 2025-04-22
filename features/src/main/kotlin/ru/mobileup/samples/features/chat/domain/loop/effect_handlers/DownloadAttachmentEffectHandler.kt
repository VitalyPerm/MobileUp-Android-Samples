package ru.mobileup.samples.features.chat.domain.loop.effect_handlers

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loop.EffectHandler
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.domain.loop.AttachmentDownloadingAction
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ChatEffect
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

class DownloadAttachmentEffectHandler(
    private val chatRepository: ChatRepository,
    private val cachedFileStorage: CachedFileStorage
) : EffectHandler<ChatEffect, ChatAction> {
    private val downloadingAttachmentMap = mutableMapOf<ChatMessageId, Job>()

    override suspend fun handleEffect(effect: ChatEffect, actionConsumer: (ChatAction) -> Unit) {
        when (effect) {
            is ChatEffect.DownloadAttachment -> downloadAttachment(
                effect.message,
                actionConsumer
            )

            is ChatEffect.CancelDownloadingAttachment -> cancelDownloadingAttachment(
                effect.messageId,
                actionConsumer
            )
            else -> {
                // Do nothing
            }
        }
    }

    private suspend fun cancelDownloadingAttachment(
        messageId: ChatMessageId,
        actionConsumer: (ChatAction) -> Unit
    ) {
        try {
            downloadingAttachmentMap[messageId]?.cancel()
            cachedFileStorage.clearCachedFileById(messageId.value)
            actionConsumer(
                AttachmentDownloadingAction.AttachmentDownloadingCancelled(
                    messageId = messageId
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            actionConsumer(
                AttachmentDownloadingAction.AttachmentDownloadingFailed(
                    messageId = messageId,
                    exception = e
                )
            )
        } finally {
            downloadingAttachmentMap.remove(messageId)
        }
    }

    private suspend fun downloadAttachment(
        message: ChatMessage,
        actionConsumer: (ChatAction) -> Unit
    ) {
        coroutineScope {
            val downloadingJob = launch(start = CoroutineStart.LAZY) {
                try {
                    message.attachment?.remoteLink?.let { url ->
                        val filename = "${UUID.randomUUID()}.${message.attachment.extension}"

                        val cachedFile = cachedFileStorage.insertCachedFile(
                            id = message.id.value,
                            time = message.time,
                            downloaded = false,
                            uploaded = true,
                            role = CachedFile.Role.CHAT_ATTACHMENT,
                            filename = filename
                        )

                        actionConsumer(
                            AttachmentDownloadingAction.AttachmentDownloadingStarted(
                                messageId = message.id,
                                attachment = message.attachment.copy(
                                    localFilePath = cachedFile.absolutePath,
                                    downloadingStatus = ChatAttachment.DownloadingStatus.InProgress
                                )
                            )
                        )

                        chatRepository.downloadAttachment(url, filename)

                        cachedFileStorage.updateDownloadingStatusById(
                            id = message.id.value,
                            downloaded = true
                        )

                        actionConsumer(
                            AttachmentDownloadingAction.AttachmentDownloaded(
                                messageId = message.id,
                                attachment = message.attachment.copy(
                                    localFilePath = cachedFile.absolutePath,
                                    downloadingStatus = ChatAttachment.DownloadingStatus.Downloaded
                                )
                            )
                        )
                        downloadingAttachmentMap.remove(message.id)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    actionConsumer(
                        AttachmentDownloadingAction.AttachmentDownloadingFailed(
                            messageId = message.id,
                            exception = e
                        )
                    )
                    downloadingAttachmentMap.remove(message.id)
                }
            }
            downloadingAttachmentMap[message.id] = downloadingJob
            downloadingJob.start()
        }
    }
}