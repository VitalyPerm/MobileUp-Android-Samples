package ru.mobileup.samples.features.video.data.utils

import android.content.Context
import androidx.camera.video.FileOutputOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val DATE_FORMAT_PATTERN_FOR_FILE = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val FILE_FORMAT_SUFFIX = ".mp4"

enum class VideoEditorDirectory(
    private val dirName: String
) {
    Recorder("recorder"),
    Render("render");

    fun toFile(context: Context): File =
        context
            .filesDir
            .resolve("video_editor_cache/$dirName")
}

fun getFileName(formatSuffix: String = FILE_FORMAT_SUFFIX) = SimpleDateFormat(
    DATE_FORMAT_PATTERN_FOR_FILE,
    Locale.US
).format(
    System.currentTimeMillis()
) + formatSuffix

fun Context.getOutputOptionForIntermediateVideo(limitTimeMillis: Long): FileOutputOptions {
    val videoFile = File(
        VideoEditorDirectory.Recorder.toFile(this),
        getFileName()
    )
    return FileOutputOptions.Builder(videoFile)
        .setDurationLimitMillis(limitTimeMillis)
        .build()
}