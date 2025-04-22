package ru.mobileup.samples.features.chat.domain.loop.utils

import co.touchlab.kermit.Logger
import me.aartikov.sesame.loop.LoopLogger
import me.aartikov.sesame.loop.Next
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ChatEffect
import ru.mobileup.samples.features.chat.domain.state.ChatState
import ru.mobileup.samples.features.chat.domain.state.ChatTag

class ChatLogger(
    private val chatTag: ChatTag
) : LoopLogger<ChatState, ChatAction, ChatEffect> {

    override fun logOnStarted(state: ChatState) {
        Logger.withTag(chatTag.value).d("Started")
    }

    override fun logBeforeReduce(state: ChatState, action: ChatAction) {
        Logger.withTag(chatTag.value).d("Handle action $action")
    }

    override fun logAfterReduce(
        previousState: ChatState,
        action: ChatAction,
        next: Next<ChatState, ChatEffect>
    ) {
        if (next.state != null) {
            Logger.withTag(chatTag.value).d("New state ${next.state}")
        }
        if (next.effects.isNotEmpty()) {
            Logger.withTag(chatTag.value).d("Effects ${next.effects}")
        }
    }
}