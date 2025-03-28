package ru.mobileup.samples.features.video.data

import android.net.Uri
import ru.mobileup.samples.features.video.data.utils.VideoDirectory

interface VideoFileManager {
    suspend fun moveVideoToMediaStore(fileUri: Uri): Uri?
    suspend fun cleanVideoDirectory(directory: VideoDirectory)
}