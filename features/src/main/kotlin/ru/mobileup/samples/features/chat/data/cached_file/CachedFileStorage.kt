package ru.mobileup.samples.features.chat.data.cached_file

import android.net.Uri
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import java.time.LocalDateTime

interface CachedFileStorage {

    suspend fun copyFileToCache(
        id: String,
        uri: Uri,
        time: LocalDateTime,
        role: CachedFile.Role
    ): CachedFile

    suspend fun clearAll()

    suspend fun checkAndClearIfNeedCachedFiles(olderThan: LocalDateTime)

    suspend fun getCachedFile(id: String): CachedFile?

    suspend fun updateUploadingStatusAndId(oldId: String, newId: String, uploaded: Boolean)

    suspend fun updateDownloadingStatusById(id: String, downloaded: Boolean)

    suspend fun insertCachedFile(
        id: String,
        time: LocalDateTime,
        filename: String,
        downloaded: Boolean,
        uploaded: Boolean,
        role: CachedFile.Role
    ): CachedFile

    suspend fun clearCachedFileById(id: String)
}