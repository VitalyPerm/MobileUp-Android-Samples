package ru.mobileup.samples.features.video.data.utils

import android.util.Size

fun Size.aspectRatio(): Float {
    return width.toFloat() / height.toFloat()
}