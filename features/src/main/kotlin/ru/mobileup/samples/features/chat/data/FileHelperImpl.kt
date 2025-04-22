package ru.mobileup.samples.features.chat.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.mobileup.samples.features.chat.data.FileHelper.Companion.CACHED_FILES_DIR
import ru.mobileup.samples.features.chat.domain.exception.FileCopingException
import ru.mobileup.samples.features.chat.domain.exception.InvalidMediaTypeException
import ru.mobileup.samples.features.chat.domain.exception.TooBigFileSizeException
import ru.mobileup.samples.features.chat.domain.cache.CachedFile
import java.io.File
import java.util.UUID

class FileHelperImpl(private val context: Context) : FileHelper {
    override val cacheDirPath
        get() = context.cacheDir.absolutePath

    private val contentResolver
        get() = context.contentResolver

    override suspend fun copyFileToCache(
        id: String,
        uri: Uri,
        time: String,
        role: CachedFile.Role
    ): CachedFile {
        return withContext(Dispatchers.IO) {
            var outputFile: File? = null
            try {
                val cachedFilesDir = File(context.cacheDir, CACHED_FILES_DIR)

                if (!cachedFilesDir.exists()) {
                    cachedFilesDir.mkdirs()
                }

                val extension = MimeTypeMap
                    .getSingleton()
                    .getExtensionFromMimeType(contentResolver.getType(uri))

                val filename = "${UUID.randomUUID()}.$extension"
                outputFile = File(cachedFilesDir, filename)

                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = outputFile.outputStream()

                inputStream.use { input ->
                    outputStream.use { output ->
                        input?.copyTo(output)
                    }
                }

                CachedFile(
                    id = id,
                    absolutePath = outputFile.absolutePath,
                    uploaded = false,
                    downloaded = true,
                    date = time,
                    role = role
                )
            } catch (e: TooBigFileSizeException) {
                throw e
            } catch (e: Exception) {
                outputFile?.let { deleteFile(it) }

                throw FileCopingException(e)
            }
        }
    }

    override suspend fun downloadCachedFile(body: ResponseBody, filename: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val cachedFilesDir = File(context.cacheDir, CACHED_FILES_DIR)
                if (!cachedFilesDir.exists()) {
                    cachedFilesDir.mkdirs()
                }

                val inputStream = body.byteStream()
                val outputFile = File(cachedFilesDir, filename)
                val outputStream = outputFile.outputStream()

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                return@withContext outputFile.absolutePath
            } catch (e: Exception) {
                throw FileCopingException(e)
            }
        }
    }

    override fun getMimeType(uri: Uri): String {
        return contentResolver.getType(uri) ?: throw InvalidMediaTypeException()
    }

    override fun getExtension(uri: Uri): String {
        val extension = MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri))
        return ".$extension"
    }

    override fun getFilename(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val filename = nameIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filename ?: "file"
    }

    override fun getFileSize(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
        cursor?.moveToFirst()
        val fileSize = sizeIndex?.let { cursor.getLong(it) }
        cursor?.close()
        return fileSize ?: 0L
    }

    private fun deleteFile(file: File) {
        try {
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteFile(relativePath: String) {
        withContext(Dispatchers.IO) {
            try {
                val outputFile = File(context.cacheDir, relativePath)
                if (outputFile.exists()) {
                    outputFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun isFileExists(relativePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val outputFile = File(context.cacheDir, relativePath)
            outputFile.exists()
        }
    }

    override suspend fun clearAllCachedFiles() {
        withContext(Dispatchers.IO) {
            val cachedFilesDir = File(context.cacheDir, CACHED_FILES_DIR)
            if (cachedFilesDir.exists()) {
                cachedFilesDir.deleteRecursively()
            }
        }
    }

    override suspend fun convertFileToBase64(localFilePath: String): String {
        return withContext(Dispatchers.IO) {
            val bytes = File(localFilePath).readBytes()
            encodeToString(bytes, NO_WRAP)
        }
    }
}