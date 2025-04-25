package ru.mobileup.samples.features.chat.domain.state.message

import ru.mobileup.samples.features.chat.domain.cache.CachedFile

data class ChatAttachment(
    val localFilePath: String? = null,
    val remoteLink: String?,
    val filename: String,
    val extension: String,
    val type: Type,
    val downloadingStatus: DownloadingStatus
) {

    enum class Type {
        IMAGE, VIDEO, FILE
    }

    sealed class DownloadingStatus {
        data object NotDownloaded : DownloadingStatus()
        data object InProgress : DownloadingStatus()
        data object Downloaded : DownloadingStatus()
        data object DownloadingCancelled : DownloadingStatus()
        data class DownloadingFailed(val exception: Exception) : DownloadingStatus()
    }
}

fun CachedFile.toChatAttachment(
    type: ChatAttachment.Type,
    extension: String,
    name: String,
    downloadingStatus: ChatAttachment.DownloadingStatus
) = ChatAttachment(
    localFilePath = absolutePath,
    remoteLink = null,
    type = type,
    downloadingStatus = downloadingStatus,
    filename = name,
    extension = extension
)