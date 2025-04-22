package ru.mobileup.samples.features.chat.domain.loop.action_sources

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.aartikov.sesame.loop.ActionSource
import ru.mobileup.samples.features.chat.data.ChatRepository
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ConnectionAction
import ru.mobileup.samples.features.chat.domain.loop.MessageReceivingAction
import ru.mobileup.samples.features.chat.domain.state.ChatTag

class ChatActionSource(
    private val chatTag: ChatTag,
    private val chatGateway: ChatRepository
) : ActionSource<ChatAction> {
    override suspend fun start(actionConsumer: (ChatAction) -> Unit) {
        coroutineScope {
            launch {
                chatGateway.isConnected.collect { connected ->
                    actionConsumer(
                        if (connected) {
                            ConnectionAction.Connected
                        } else {
                            ConnectionAction.Disconnected
                        }
                    )
                }
            }

            launch {
                chatGateway.getMessagesFlow(chatTag).collect { message ->
                    actionConsumer(MessageReceivingAction.MessageReceived(message))
                }
            }
        }
    }
}