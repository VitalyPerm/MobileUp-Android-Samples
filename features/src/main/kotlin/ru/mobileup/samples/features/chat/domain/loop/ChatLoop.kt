package ru.mobileup.samples.features.chat.domain.loop

import me.aartikov.sesame.loop.Loop
import ru.mobileup.samples.features.chat.domain.state.ChatState

internal typealias ChatLoop = Loop<ChatState, ChatAction, ChatEffect>