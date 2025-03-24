package ru.mobileup.samples.features.photo.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mobileup.samples.features.photo.data.utils.PhotoDirectory
import ru.mobileup.samples.features.photo.data.utils.getPhotoFileName
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private const val PHOTO_MIME_TYPE = "image/jpeg"
private const val APP_DIRECTORY = "MobileUp"
internal const val RELATIVE_STORAGE_PATH = "Pictures/$APP_DIRECTORY"

class PhotoFileManagerImpl(
    private val context: Context
) : PhotoFileManager {

    override suspend fun movePhotoToMediaStore(fileUri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                moveFileToMediaStoreApi29(fileUri)
            } else {
                moveFileToMediaStore(fileUri)
            }
        }
    }

    override suspend fun cleanPhotoDirectory(directory: PhotoDirectory) {
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
            put(MediaStore.MediaColumns.DISPLAY_NAME, getPhotoFileName())
            put(MediaStore.MediaColumns.MIME_TYPE, PHOTO_MIME_TYPE)
            put(MediaStore.Images.Media.RELATIVE_PATH, RELATIVE_STORAGE_PATH)
        }

        val newUri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
                Log.e("recording move file: ", e.toString())
            }
        }

        return newUri
    }

    private fun moveFileToMediaStore(fileUri: Uri): Uri? {
        val appDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            APP_DIRECTORY
        )

        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }

        val destinationFile = File(appDirectory, getPhotoFileName())

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