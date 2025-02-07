package ru.mobileup.samples.features.video.domain

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Immutable
data class VideoTransform(
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val offsetPercent: Offset = Offset.Zero,
) {
    companion object {
        val defaultValue = VideoTransform()
    }
}

fun VideoTransform.rotate(value: Float): Float {
    var newValue = (this.rotation + value)
    if (abs(newValue) == 360f) return 0f
    val remainder = newValue % 360f
    if (abs(remainder) > 0f) {
        newValue = remainder
    }
    return newValue
}

fun Offset.rotate(rotation: Float): Offset {
    val radians = Math.toRadians(rotation.toDouble())
    val sinTheta = sin(radians)
    val cosTheta = cos(radians)

    val rotatedDx = (this.x * cosTheta - this.y * sinTheta).toFloat()
    val rotatedDy = (this.x * sinTheta + this.y * cosTheta).toFloat()

    return Offset(rotatedDx, rotatedDy)
}