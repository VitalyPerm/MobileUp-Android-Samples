package ru.mobileup.samples.features.video.data

import android.net.Uri
import ru.mobileup.samples.features.video.data.utils.VideoEditorDirectory

interface FileManager {
    suspend fun moveVideoToMediaStore(fileUri: Uri): Uri?
    suspend fun deleteEditorDirectory(directory: VideoEditorDirectory)
}