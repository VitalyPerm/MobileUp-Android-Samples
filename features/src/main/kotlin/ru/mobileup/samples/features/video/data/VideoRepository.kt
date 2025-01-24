package ru.mobileup.samples.features.video.data

import android.net.Uri
import android.util.Size

interface VideoRepository {
    fun getVideoDurationMsByUri(uri: Uri): Long
    fun getVideoSizeByUri(uri: Uri): Size
}