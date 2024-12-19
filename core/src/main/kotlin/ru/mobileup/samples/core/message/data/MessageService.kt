package ru.mobileup.samples.core.message.data

import kotlinx.coroutines.flow.Flow
import ru.mobileup.samples.core.message.domain.Message

/**
 * A service for centralized message showing
 */
interface MessageService {

    val messageFlow: Flow<Message>

    fun showMessage(message: Message)
}