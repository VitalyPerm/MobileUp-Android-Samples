package ru.mobileup.samples.features.ar.core.helpers

import android.os.Build
import android.view.WindowManager
import com.google.ar.core.Camera
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.TrackingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.activity.ActivityProvider

class ARTrackingStateHelper(
    private val activityProvider: ActivityProvider
) {
    private companion object {
        const val INSUFFICIENT_FEATURES_MESSAGE =
            "Can't find anything. Aim device at a surface with more texture or color."
        const val BAD_STATE_MESSAGE =
            "Tracking lost due to bad internal state. Please try restarting the AR experience."

        const val INSUFFICIENT_LIGHT_MESSAGE =
            "Too dark. Try moving to a well-lit area."

        const val INSUFFICIENT_LIGHT_ANDROID_S_MESSAGE =
            "Too dark. Try moving to a well-lit area."

        const val EXCESSIVE_MOTION_MESSAGE = "Moving too fast. Slow down."

        const val CAMERA_UNAVAILABLE_MESSAGE =
            "Another app is using the camera. Tap on this app or try closing the other one."
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var previousTrackingState: TrackingState? = null

    fun updateKeepScreenOnFlag(trackingState: TrackingState) {
        if (trackingState == previousTrackingState) return
        previousTrackingState = trackingState
        when (trackingState) {
            TrackingState.TRACKING -> setScreenOnFlag(true)
            TrackingState.PAUSED,
            TrackingState.STOPPED -> setScreenOnFlag(false)
        }
    }

    fun getTrackingFailureReasonString(camera: Camera): String {
        val reason = camera.trackingFailureReason
        return when (reason) {
            TrackingFailureReason.NONE -> ""
            TrackingFailureReason.BAD_STATE -> BAD_STATE_MESSAGE
            TrackingFailureReason.INSUFFICIENT_LIGHT ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    INSUFFICIENT_LIGHT_MESSAGE
                } else {
                    INSUFFICIENT_LIGHT_ANDROID_S_MESSAGE
                }
            TrackingFailureReason.EXCESSIVE_MOTION -> EXCESSIVE_MOTION_MESSAGE
            TrackingFailureReason.INSUFFICIENT_FEATURES -> INSUFFICIENT_FEATURES_MESSAGE
            TrackingFailureReason.CAMERA_UNAVAILABLE -> CAMERA_UNAVAILABLE_MESSAGE
        }
    }

    private fun setScreenOnFlag(isScreenOn: Boolean) {
        scope.launch {
            activityProvider.awaitActivity().window.run {
                val flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                if (isScreenOn) addFlags(flag) else clearFlags(flag)
            }
        }
    }
}