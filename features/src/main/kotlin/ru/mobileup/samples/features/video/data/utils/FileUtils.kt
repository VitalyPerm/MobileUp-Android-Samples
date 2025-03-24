package ru.mobileup.samples.features.video.data.utils

import android.content.Context
import androidx.camera.video.FileOutputOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT_PATTERN_FOR_FILE = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val FILE_FORMAT_SUFFIX = ".mp4"

enum class VideoDirectory(
    private val dirName: String
) {
    Recorder("recorder"),
    Render("render");

    fun toFile(context: Context): File =
        context
            .filesDir
            .resolve("video_cache/$dirName")
}

fun getVideoFileName(formatSuffix: String = FILE_FORMAT_SUFFIX) = SimpleDateFormat(
    DATE_FORMAT_PATTERN_FOR_FILE,
    Locale.US
).format(
    System.currentTimeMillis()
) + formatSuffix

fun Context.getOutputOptionForIntermediateVideo(limitTimeMillis: Long): FileOutputOptions {
    val videoFile = File(
        VideoDirectory.Recorder.toFile(this),
        getVideoFileName()
    )
    return FileOutputOptions.Builder(videoFile)
        .setDurationLimitMillis(limitTimeMillis)
        .build()
}