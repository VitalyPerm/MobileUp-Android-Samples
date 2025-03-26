package ru.mobileup.samples.features.photo.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mobileup.samples.features.photo.data.utils.PhotoDirectory
import ru.mobileup.samples.features.photo.data.utils.getPhotoFileName
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private const val TAG = "PhotoFileManager"
private const val PHOTO_MIME_TYPE = "image/jpeg"
private const val APP_DIRECTORY = "MobileUp"
private const val RELATIVE_STORAGE_PATH = "Pictures/$APP_DIRECTORY"
private const val QUALITY_ORIGINAL = 100

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
                val orientation: Int = ExifInterface(fileUri.path!!).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                resolver.openInputStream(fileUri)?.use { input ->
                    resolver.openOutputStream(newUri)?.use { output ->
                        rotateBitmap(BitmapFactory.decodeStream(input), orientation).apply {
                            compress(Bitmap.CompressFormat.JPEG, QUALITY_ORIGINAL, output)
                        }
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

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.postRotate(180f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.postScale(-1f, 1f)
            }
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}