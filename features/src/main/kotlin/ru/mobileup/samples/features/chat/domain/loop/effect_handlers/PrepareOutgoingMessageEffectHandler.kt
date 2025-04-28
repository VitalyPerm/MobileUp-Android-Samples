package ru.mobileup.samples.features.chat.domain.loop.effect_handlers

import android.net.Uri
import me.aartikov.sesame.loop.EffectHandler
import ru.mobileup.samples.features.chat.data.FileHelper
import ru.mobileup.samples.features.chat.data.cached_file.CachedFileStorage
import ru.mobileup.samples.features.chat.domain.exception.TooBigFileSizeException
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ChatEffect
import ru.mobileup.samples.features.chat.domain.loop.MessagePreparingAction
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import ru.mobileup.samples.features.chat.domain.state.message.ChatAttachment
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessage
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId
import ru.mobileup.samples.features.chat.domain.state.message.DownloadingStatus
import ru.mobileup.samples.features.chat.domain.state.message.MessageAuthor
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus
import ru.mobileup.samples.features.chat.domain.state.message.toChatAttachment
import java.time.LocalDateTime
import kotlin.coroutines.cancellation.CancellationException

class PrepareOutgoingMessageEffectHandler(
    private val storage: CachedFileStorage,
    private val fileHelper: FileHelper
) : EffectHandler<ChatEffect, ChatAction> {
    companion object {
        const val SIZE_LIMIT = 10485760L
    }

    override suspend fun handleEffect(effect: ChatEffect, actionConsumer: (ChatAction) -> Unit) {
        when (effect) {
            is ChatEffect.PrepareOutgoingTextMessage -> {
                val message = prepareOutgoingTextMessage(effect.text)
                actionConsumer(MessagePreparingAction.OutgoingMessagePrepared(message))
            }

            is ChatEffect.PrepareOutgoingAttachmentMessage -> {
                prepareOutgoingAttachmentMessage(effect.uri, actionConsumer)
            }

            else -> {
                // Do nothing
            }
        }
    }

    private fun prepareOutgoingTextMessage(text: String): ChatMessage {
        return ChatMessage(
            id = ChatMessageId.generateLocalId(),
            author = MessageAuthor.Me,
            text = text,
            time = LocalDateTime.now(),
            messageStatus = MessageStatus.Sending
        )
    }

    private suspend fun prepareOutgoingAttachmentMessage(
        uri: Uri,
        actionConsumer: (ChatAction) -> Unit
    ) {
        try {
            val id = ChatMessageId.generateLocalId()

            val chatAttachment = cacheAttachment(
                id = id,
                uri = uri,
                time = LocalDateTime.now(),
                role = CachedFile.Role.CHAT_ATTACHMENT
            )

            val message = ChatMessage(
                id = id,
                author = MessageAuthor.Me,
                text = "",
                time = LocalDateTime.now(),
                messageStatus = MessageStatus.Sending,
                attachment = chatAttachment
            )
            actionConsumer(MessagePreparingAction.OutgoingMessagePrepared(message))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            actionConsumer(MessagePreparingAction.OutgoingMessagePreparingFailed(e))
        }
    }

    private suspend fun cacheAttachment(
        id: ChatMessageId,
        uri: Uri,
        time: LocalDateTime,
        role: CachedFile.Role
    ): ChatAttachment {
        val size = fileHelper.getFileSize(uri)
        if (size > SIZE_LIMIT) throw TooBigFileSizeException()

        val cachedFile = storage.copyFileToCache(id.value, uri, time, role)

        val extension = fileHelper.getExtension(uri)
        val filename = fileHelper.getFilename(uri)
        val mimeType = fileHelper.getMimeType(uri)
        val type = when {
            mimeType.startsWith("image") -> ChatAttachment.Type.IMAGE
            mimeType.startsWith("video") -> ChatAttachment.Type.VIDEO
            else -> ChatAttachment.Type.FILE
        }

        return cachedFile.toChatAttachment(
            type = type,
            extension = extension,
            name = filename,
            downloadingStatus = DownloadingStatus.Downloaded
        )
    }
}