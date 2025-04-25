package ru.mobileup.samples.features.chat.domain.loop.effect_handlers

import me.aartikov.sesame.loop.EffectHandler
import ru.mobileup.samples.features.chat.domain.loop.ChatAction
import ru.mobileup.samples.features.chat.domain.loop.ChatEffect
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect

class ExternalChatEffectHandler(
    private val consumer: (ExternalChatEffect) -> Unit
) : EffectHandler<ChatEffect, ChatAction> {

    override suspend fun handleEffect(effect: ChatEffect, actionConsumer: (ChatAction) -> Unit) {
        if (effect is ExternalChatEffect) {
            consumer(effect)
        }
    }
}