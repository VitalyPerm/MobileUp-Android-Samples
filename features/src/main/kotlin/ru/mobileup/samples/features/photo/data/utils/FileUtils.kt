package ru.mobileup.samples.features.photo.data.utils

import android.content.Context
import java.io.File
import java.util.UUID

private const val FILE_FORMAT_SUFFIX = ".jpg"

enum class PhotoDirectory(
    private val dirName: String
) {
    Camera("camera");

    fun toFile(context: Context): File =
        context
            .filesDir
            .resolve("photo_cache/$dirName")
}

fun getPhotoFileName(formatSuffix: String = FILE_FORMAT_SUFFIX) =
    UUID.randomUUID().toString() + formatSuffix

fun Context.getOutputFileForPhoto(): File {
    return File(
        PhotoDirectory.Camera.toFile(this),
        getPhotoFileName()
    )
}