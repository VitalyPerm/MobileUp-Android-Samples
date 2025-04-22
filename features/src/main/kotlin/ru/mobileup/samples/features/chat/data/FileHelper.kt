package ru.mobileup.samples.features.chat.data

import android.net.Uri
import okhttp3.ResponseBody
import ru.mobileup.samples.features.chat.domain.cache.CachedFile

interface FileHelper {

    companion object {
        const val CACHED_FILES_DIR = "cached_files"
    }

    val cacheDirPath: String

    suspend fun copyFileToCache(
        id: String,
        uri: Uri,
        time: String,
        role: CachedFile.Role
    ): CachedFile

    suspend fun downloadCachedFile(body: ResponseBody, filename: String): String

    fun getMimeType(uri: Uri): String

    fun getExtension(uri: Uri): String

    fun getFilename(uri: Uri): String

    fun getFileSize(uri: Uri): Long

    suspend fun deleteFile(relativePath: String)

    suspend fun isFileExists(relativePath: String): Boolean

    suspend fun clearAllCachedFiles()

    suspend fun convertFileToBase64(localFilePath: String): String
}