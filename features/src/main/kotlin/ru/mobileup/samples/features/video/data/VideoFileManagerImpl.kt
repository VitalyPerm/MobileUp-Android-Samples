package ru.mobileup.samples.features.video.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mobileup.samples.features.video.data.utils.VideoDirectory
import ru.mobileup.samples.features.video.data.utils.getVideoFileName
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private const val TAG = "PhotoFileManager"
private const val VIDEO_MIME_TYPE = "video/mp4"
private const val APP_DIRECTORY = "MobileUp"
internal const val RELATIVE_STORAGE_PATH = "Movies/$APP_DIRECTORY"

class VideoFileManagerImpl(
    private val context: Context
) : VideoFileManager {

    override suspend fun moveVideoToMediaStore(fileUri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                moveFileToMediaStoreApi29(fileUri)
            } else {
                moveFileToMediaStore(fileUri)
            }
        }
    }

    override suspend fun cleanVideoDirectory(
        directory: VideoDirectory,
    ) {
        withContext(Dispatchers.IO) {
            try {
                directory
                    .toFile(context)
                    .deleteRecursively()
            } catch (_: Exception) {
                // Do nothing
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun moveFileToMediaStoreApi29(fileUri: Uri): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, getVideoFileName())
            put(MediaStore.MediaColumns.MIME_TYPE, VIDEO_MIME_TYPE)
            put(MediaStore.Video.Media.RELATIVE_PATH, RELATIVE_STORAGE_PATH)
        }

        val newUri = resolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (newUri != null) {
            try {
                resolver.openInputStream(fileUri)?.use { input ->
                    resolver.openOutputStream(newUri)?.use { output ->
                        input.copyTo(output, DEFAULT_BUFFER_SIZE)
                    }
                }
            } catch (e: Exception) {
                Logger.withTag(TAG).e("Record failed $e")
            }
        }

        return newUri
    }

    private fun moveFileToMediaStore(fileUri: Uri): Uri? {
        val appDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            APP_DIRECTORY
        )

        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }

        val destinationFile = File(appDirectory, getVideoFileName())

        return try {
            FileInputStream(fileUri.toFile()).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream, DEFAULT_BUFFER_SIZE)
                }
            }
            destinationFile.toUri()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}