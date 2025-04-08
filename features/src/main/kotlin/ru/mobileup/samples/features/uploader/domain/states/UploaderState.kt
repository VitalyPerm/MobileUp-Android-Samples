package ru.mobileup.samples.features.uploader.domain.states

import android.net.Uri
import ru.mobileup.samples.features.uploader.domain.progress.DownloadProgress
import ru.mobileup.samples.features.uploader.domain.progress.UploadProgress

data class UploaderState(
    val uri: Uri? = null,
    val uploadProgress: UploadProgress? = null,
    val downloadProgress: DownloadProgress? = null
)