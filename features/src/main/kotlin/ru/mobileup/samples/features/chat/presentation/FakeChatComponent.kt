package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import ru.mobileup.samples.core.dialog.standard.fakeStandardDialogControl
import ru.mobileup.samples.core.utils.fakeInputControl
import ru.mobileup.samples.features.chat.domain.loop.ExternalChatEffect
import ru.mobileup.samples.features.chat.domain.state.ChatState
import ru.mobileup.samples.features.chat.domain.state.message.ChatMessageId

class FakeChatComponent : ChatComponent {
    override val chatState = MutableStateFlow(ChatState.INITIAL)
    override val effectFlow: SharedFlow<ExternalChatEffect> = MutableSharedFlow()
    override val inputControl = fakeInputControl()
    override val messageSendFailedDialog = fakeStandardDialogControl()
    override fun onReloadClick() = Unit
    override fun onSendMessage() = Unit
    override fun onSendFile(uri: Uri) = Unit
    override fun onMessageClick(messageId: ChatMessageId) = Unit
    override fun onMessageSendFailed(messageId: ChatMessageId) = Unit
    override fun onError(exception: Exception) = Unit
}