package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.mobileup.samples.core.utils.fakeInputControl
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.state.ChatState

class FakeChatComponent : ChatComponent {
    override val inputControl = fakeInputControl()
    override val chatState = MutableStateFlow(ChatState.INITIAL)
    override val effectFlow: SharedFlow<ExternalChatEffect> = MutableSharedFlow()
    override fun onSendMessage() = Unit
    override fun onSendFile(uri: Uri) = Unit
}