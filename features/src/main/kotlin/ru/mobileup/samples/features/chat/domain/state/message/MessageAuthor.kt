package ru.mobileup.samples.features.chat.domain.state.message

import dev.icerock.moko.resources.desc.StringDesc

sealed interface MessageAuthor {
    data object Me : MessageAuthor
    data class User(
        val title: StringDesc
    ) : MessageAuthor
}