package ru.mobileup.samples.features.video.domain

import android.net.Uri

sealed class VideoOption {
    data object Recorder : VideoOption()
    data class Player(val uri: Uri) : VideoOption()
}