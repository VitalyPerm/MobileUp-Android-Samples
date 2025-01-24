package ru.mobileup.samples.core.utils

fun millisToMS(millis: Long?) = if (millis != null && (millis < 3600000)) {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    String.format("%02d:%02d", minutes, remainingSeconds)
} else {
    ""
}