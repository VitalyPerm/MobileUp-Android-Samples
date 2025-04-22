package ru.mobileup.samples.features.chat.domain.cache

data class CachedFile(
    val id: String,
    val absolutePath: String,
    val uploaded: Boolean,
    val downloaded: Boolean,
    val date: String,
    val role: Role
) {

    enum class Role {
        CHAT_ATTACHMENT
    }
}