package ru.mobileup.samples.features.chat.domain.loop

import me.aartikov.sesame.loop.Next
import me.aartikov.sesame.loop.Reducer
import me.aartikov.sesame.loop.effects
import me.aartikov.sesame.loop.next
import me.aartikov.sesame.loop.nothing
import ru.mobileup.samples.features.chat.domain.state.ChatState
import ru.mobileup.samples.features.chat.domain.state.addMessage
import ru.mobileup.samples.features.chat.domain.state.addPendingMessage
import ru.mobileup.samples.features.chat.domain.state.changeAttachmentMessage
import ru.mobileup.samples.features.chat.domain.state.changeAttachmentMessageDownloadingStatus
import ru.mobileup.samples.features.chat.domain.state.changePendingMessageSendingStatus
import ru.mobileup.samples.features.chat.domain.state.mergeWithLoadedHistory
import ru.mobileup.samples.features.chat.domain.state.message.DownloadingStatus
import ru.mobileup.samples.features.chat.domain.state.message.MessageStatus
import ru.mobileup.samples.features.chat.domain.state.removePendingMessage
import ru.mobileup.samples.features.chat.domain.state.setActive
import ru.mobileup.samples.features.chat.domain.state.setConnected
import ru.mobileup.samples.features.chat.domain.state.setHistoryLoaded
import ru.mobileup.samples.features.chat.domain.state.setHistoryLoadingError
import ru.mobileup.samples.features.chat.domain.state.setHistoryLoadingInProgress

class ChatReducer : Reducer<ChatState, ChatAction, ChatEffect> {
    override fun reduce(state: ChatState, action: ChatAction): Next<ChatState, ChatEffect> =
        when (action) {
            is LifecycleAction -> reduceLifecycleAction(state, action)
            is ConnectionAction -> reduceConnectionAction(state, action)
            is UserAction -> reduceUserAction(state, action)
            is MessagePreparingAction -> reduceMessagePreparingAction(state, action)
            is MessageReceivingAction -> reduceMessageReceivingAction(state, action)
            is MessageSendingAction -> reduceMessageSendingAction(state, action)
            is HistoryLoadingAction -> reduceHistoryLoadingAction(state, action)
            is AttachmentDownloadingAction -> reduceAttachmentLoadingAction(state, action)
        }

    private fun reduceLifecycleAction(
        state: ChatState,
        action: LifecycleAction
    ): Next<ChatState, ChatEffect> = when (action) {

        is LifecycleAction.Initialize -> effects(
            loadHistoryIfRequired(state)
        )

        is LifecycleAction.Activate -> {
            val newState = state.setActive(true)
            next(
                newState,
                loadHistoryIfRequired(newState)
            )
        }

        is LifecycleAction.Deactivate -> next(
            state.setActive(false)
        )
    }

    private fun reduceConnectionAction(
        state: ChatState,
        action: ConnectionAction
    ): Next<ChatState, ChatEffect> = when (action) {

        is ConnectionAction.Connected -> {
            val newState = state.setConnected(true)
            next(
                newState,
                loadHistoryIfRequired(newState)
            )
        }

        is ConnectionAction.Disconnected -> next(
            state.setConnected(false)
        )
    }

    private fun reduceUserAction(
        state: ChatState,
        action: UserAction
    ): Next<ChatState, ChatEffect> = when (action) {
        is UserAction.ReloadHistory -> effects(
            loadHistoryIfRequired(state, force = true)
        )

        is UserAction.SendText -> effects(
            ChatEffect.PrepareOutgoingTextMessage(action.text)
        )

        is UserAction.AttachFile -> effects(
            ChatEffect.PrepareOutgoingAttachmentMessage(action.uri)
        )

        is UserAction.ScrolledToNotDownloadedImage -> {
            when (val message = state.findMessage(action.messageId)) {
                null -> nothing()
                else -> effects(ChatEffect.DownloadAttachment(message))
            }
        }

        is UserAction.MessageClick -> {
            val message = state.findMessage(action.messageId)
            when {
                message == null -> nothing()

                message.messageStatus is MessageStatus.Failed ->
                    effects(ExternalChatEffect.ShowFailedMessageDialog(message.id))

                message.attachment?.downloadingStatus == DownloadingStatus.InProgress ->
                    effects(ChatEffect.CancelDownloadingAttachment(action.messageId))

                message.messageStatus == MessageStatus.Sending ->
                    effects(ChatEffect.CancelSendingAttachment(action.messageId))

                message.attachment?.localFilePath != null && message.attachment.downloadingStatus == DownloadingStatus.Downloaded ->
                    effects(ExternalChatEffect.OpenAttachmentFile(message.attachment.localFilePath))

                message.attachment?.downloadingStatus == DownloadingStatus.NotDownloaded ||
                        message.attachment?.downloadingStatus == DownloadingStatus.DownloadingCancelled ||
                        message.attachment?.downloadingStatus is DownloadingStatus.DownloadingFailed ->
                    effects(ChatEffect.DownloadAttachment(message))

                else -> nothing()
            }
        }

        is UserAction.ResendPendingMessage -> {
            val message = state.findPendingMessage(action.messageId)
            if (message != null) {
                next(
                    state.changePendingMessageSendingStatus(
                        action.messageId,
                        MessageStatus.Sending
                    ),
                    ChatEffect.SendMessage(message)
                )
            } else {
                nothing()
            }
        }

        is UserAction.RemovePendingMessage -> {
            next(
                state.removePendingMessage(action.messageId),
                ChatEffect.CancelSendingAttachment(action.messageId)
            )
        }
    }

    private fun reduceMessagePreparingAction(
        state: ChatState,
        action: MessagePreparingAction
    ): Next<ChatState, ChatEffect> = when (action) {
        is MessagePreparingAction.OutgoingMessagePrepared -> next(
            state.addPendingMessage(action.message),
            ChatEffect.SendMessage(action.message),
            ExternalChatEffect.ReactToAddedMessages(pendingMessage = true)
        )

        is MessagePreparingAction.OutgoingMessagePreparingFailed -> effects(
            ExternalChatEffect.ShowError(exception = action.exception)
        )
    }

    private fun reduceMessageReceivingAction(
        state: ChatState,
        action: MessageReceivingAction
    ): Next<ChatState, ChatEffect> =
        when (action) {
            is MessageReceivingAction.MessageReceived -> next(
                state.addMessage(action.message),
                ExternalChatEffect.ReactToAddedMessages(pendingMessage = false)
            )
        }

    private fun reduceMessageSendingAction(
        state: ChatState,
        action: MessageSendingAction
    ): Next<ChatState, ChatEffect> =
        when (action) {
            is MessageSendingAction.MessageSent -> next(
                state.removePendingMessage(action.pendingMessageId)
                    .addMessage(action.newMessage)
            )

            is MessageSendingAction.MessageSendingFailed -> next(
                state.changePendingMessageSendingStatus(
                    action.pendingMessageId,
                    MessageStatus.Failed(action.exception)
                )
            )

            is MessageSendingAction.AttachmentSendingCancelled -> next(
                state.removePendingMessage(action.pendingMessageId)
            )
        }

    private fun reduceHistoryLoadingAction(
        state: ChatState,
        action: HistoryLoadingAction
    ): Next<ChatState, ChatEffect> = when (action) {

        is HistoryLoadingAction.HistoryLoadingStarted -> next(
            state.setHistoryLoadingInProgress(true)
        )

        is HistoryLoadingAction.HistoryTransforming -> effects(
            ChatEffect.TransformLoadedHistory(action.messages, state.chatMessagesState.messages)
        )

        is HistoryLoadingAction.HistoryLoaded -> {
            val newState = state
                .mergeWithLoadedHistory(action.messages)
                .setHistoryLoaded()

            next(
                newState,
                if (newState.chatMessagesState.messages.size > state.chatMessagesState.messages.size) {
                    ExternalChatEffect.ReactToAddedMessages(pendingMessage = false)
                } else {
                    null
                }
            )
        }

        is HistoryLoadingAction.HistoryLoadingFailed -> next(
            state
                .setHistoryLoadingError(action.exception)
                .setHistoryLoadingInProgress(false),
            ExternalChatEffect.ShowError(action.exception)
        )
    }

    private fun reduceAttachmentLoadingAction(
        state: ChatState,
        action: AttachmentDownloadingAction
    ): Next<ChatState, ChatEffect> = when (action) {
        is AttachmentDownloadingAction.AttachmentDownloadingStarted -> next(
            state.changeAttachmentMessage(
                action.messageId,
                action.attachment
            )
        )

        is AttachmentDownloadingAction.AttachmentDownloaded -> next(
            state.changeAttachmentMessage(
                action.messageId,
                action.attachment
            )
        )

        is AttachmentDownloadingAction.AttachmentDownloadingCancelled -> next(
            state.changeAttachmentMessageDownloadingStatus(
                action.messageId,
                DownloadingStatus.DownloadingCancelled
            )
        )

        is AttachmentDownloadingAction.AttachmentDownloadingFailed -> next(
            state.changeAttachmentMessageDownloadingStatus(
                action.messageId,
                DownloadingStatus.DownloadingFailed(action.exception)
            )
        )
    }

    private fun loadHistoryIfRequired(state: ChatState, force: Boolean = false): ChatEffect? {
        val required = when {
            state.historyLoadingState.inProgress -> false
            force -> true
            !state.historyLoadingState.loaded -> true
            else -> state.connected && state.active
        }

        return if (required) ChatEffect.LoadHistory else null
    }
}