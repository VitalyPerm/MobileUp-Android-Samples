package ru.mobileup.samples.features.chat.domain.state.message

sealed class MessageStatus {
    data object Sent : MessageStatus()
    data object Sending : MessageStatus()
    data class Failed(val exception: Exception) : MessageStatus()
}