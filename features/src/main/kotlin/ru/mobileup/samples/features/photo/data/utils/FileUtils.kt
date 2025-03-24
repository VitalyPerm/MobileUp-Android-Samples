package ru.mobileup.samples.features.photo.data.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT_PATTERN_FOR_FILE = "yyyy-MM-dd-HH-mm-ss-SSS"
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

fun getPhotoFileName(formatSuffix: String = FILE_FORMAT_SUFFIX) = SimpleDateFormat(
    DATE_FORMAT_PATTERN_FOR_FILE,
    Locale.US
).format(
    System.currentTimeMillis()
) + formatSuffix

fun Context.getOutputFileForPhoto(): File {
    return File(
        PhotoDirectory.Camera.toFile(this),
        getPhotoFileName()
    )
}