package ru.mobileup.samples.core.message.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.message.domain.Message

/**
 * A component for centralized message showing. There should be only one instance
 * of this component in the app connected to the root component.
 */
interface MessageComponent {

    val visibleMessage: StateFlow<Message?>

    fun onActionClick()
}
