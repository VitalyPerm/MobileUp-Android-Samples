package ru.mobileup.samples.core.biometric.data

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import ru.mobileup.samples.core.R
import ru.mobileup.samples.core.biometric.domain.BiometricAuthResult
import ru.mobileup.samples.core.biometric.domain.BiometricSupportStatus
import ru.mobileup.samples.core.biometric.domain.BiometricType

interface BiometricService {

    val biometricType: BiometricType

    fun startBiometricAuth(
        title: StringDesc?,
        description: StringDesc?,
        negativeButtonText: StringDesc? = R.string.biometric_prompt_negative_button_text.strResDesc(),
        callback: (authStatus: BiometricAuthResult) -> Unit
    )

    fun getBiometricSupportStatus(): BiometricSupportStatus
}
