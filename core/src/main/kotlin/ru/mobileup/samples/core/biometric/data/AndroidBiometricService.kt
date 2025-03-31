package ru.mobileup.samples.core.biometric.data

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import dev.icerock.moko.resources.desc.StringDesc
import ru.mobileup.samples.core.activity.ActivityProvider
import ru.mobileup.samples.core.biometric.domain.BiometricAuthResult
import ru.mobileup.samples.core.biometric.domain.BiometricSupportStatus
import ru.mobileup.samples.core.biometric.domain.BiometricType

class AndroidBiometricService(
    private val context: Context,
    private val activityProvider: ActivityProvider
) : BiometricService {

    override val biometricType = BiometricType.Fingerprint

    private val biometricManager = BiometricManager.from(context)
    private val executor = ContextCompat.getMainExecutor(context)

    override fun getBiometricSupportStatus(): BiometricSupportStatus {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricSupportStatus.Supported
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricSupportStatus.NotEnrolled
            else -> BiometricSupportStatus.NotSupported
        }
    }

    override fun startBiometricAuth(
        title: StringDesc?,
        description: StringDesc?,
        negativeButtonText: StringDesc?,
        callback: (authResult: BiometricAuthResult) -> Unit
    ) {
        val biometricPrompt = createBiometricPrompt(callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .apply {
                title?.let { setTitle(it.toString(context)) }
                description?.let { setDescription(it.toString(context)) }
                negativeButtonText?.let { setNegativeButtonText(it.toString(context)) }
            }
            .build()
        biometricPrompt?.authenticate(promptInfo)
    }

    private fun createBiometricPrompt(
        callback: (authResult: BiometricAuthResult) -> Unit
    ): BiometricPrompt? {
        val activity = activityProvider.activity ?: return null
        return BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    val status = when (errorCode) {
                        BiometricPrompt.ERROR_LOCKOUT -> BiometricAuthResult.TooManyAttempts
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricAuthResult.Cancel
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> BiometricAuthResult.Cancel

                        else -> BiometricAuthResult.Failed
                    }
                    callback.invoke(status)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)
                    callback.invoke(BiometricAuthResult.Success)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.invoke(BiometricAuthResult.Failed)
                }
            }
        )
    }
}
