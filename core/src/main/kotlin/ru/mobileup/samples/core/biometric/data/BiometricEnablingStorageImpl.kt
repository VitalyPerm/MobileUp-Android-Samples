package ru.mobileup.samples.core.biometric.data

import ru.mobileup.samples.core.biometric.domain.BiometricEnableStatus
import ru.mobileup.samples.core.settings.SettingsFactory

class BiometricEnablingStorageImpl(settingsFactory: SettingsFactory) : BiometricEnablingStorage {

    companion object {
        private const val KEY_BIOMETRIC_ENABLED = "key_biometric_enabled"
    }

    private val settings = settingsFactory.createEncryptedSettings("biometric")

    override suspend fun putBiometricEnableStatus(
        biometricEnableStatus: BiometricEnableStatus
    ) {
        settings.putString(
            KEY_BIOMETRIC_ENABLED,
            biometricEnableStatus.mapToString()
        )
    }

    override suspend fun getBiometricEnableStatus(): BiometricEnableStatus {
        return settings
            .getString(KEY_BIOMETRIC_ENABLED)
            ?.mapToBiometricEnableStatus()
            ?: return BiometricEnableStatus.Unknown
    }
}

private fun BiometricEnableStatus.mapToString() = when (this) {
    BiometricEnableStatus.Enabled -> "enabled"
    BiometricEnableStatus.Disabled -> "disabled"
    BiometricEnableStatus.Unknown -> "unknown"
}

private fun String.mapToBiometricEnableStatus() = when (this) {
    "enabled" -> BiometricEnableStatus.Enabled
    "disabled" -> BiometricEnableStatus.Disabled
    else -> BiometricEnableStatus.Unknown
}
