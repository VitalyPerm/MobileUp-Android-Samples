package ru.mobileup.samples.features.pin_code.presentation.settings_main

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl

interface PinCodeSettingsMainComponent {

    val isPinCodeEnabled: StateFlow<Boolean>

    val isBiometricEnabled: StateFlow<Boolean>

    val isBiometricSwitchVisible: StateFlow<Boolean>

    val biometricDialogControl: StandardDialogControl

    fun onPinCodeEnabledChanged(isEnabled: Boolean)

    fun onBiometricEnabledChanged(isEnabled: Boolean)

    sealed interface Output {
        data object CreatePinCodeRequested : Output
    }
}