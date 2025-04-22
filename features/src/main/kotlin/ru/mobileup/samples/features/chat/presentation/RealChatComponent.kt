package ru.mobileup.samples.features.chat.presentation

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.options.KeyboardOptions
import ru.mobileup.kmm_form_validation.options.KeyboardType
import ru.mobileup.kmm_form_validation.options.VisualTransformation
import ru.mobileup.samples.core.utils.InputControl
import ru.mobileup.samples.features.chat.domain.ChatClient

private const val INPUT_MAX_LENGTH = 400

class RealChatComponent(
    componentContext: ComponentContext,
    private val chatClient: ChatClient
) : ComponentContext by componentContext, ChatComponent {

    override val chatState = chatClient.stateFlow

    override val effectFlow = chatClient.effectFlow

    override val inputControl = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        textTransformation = { text -> text },
        visualTransformation = VisualTransformation.None,
        maxLength = INPUT_MAX_LENGTH
    )

    override fun onSendMessage() {
        chatClient.sendText(inputControl.value.value)
        inputControl.onValueChange("")
    }

    override fun onSendFile(uri: Uri) {
        chatClient.attachFile(uri)
    }
}