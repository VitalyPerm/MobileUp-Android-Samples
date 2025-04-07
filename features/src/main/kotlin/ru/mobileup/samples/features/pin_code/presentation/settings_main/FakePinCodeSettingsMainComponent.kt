package ru.mobileup.samples.features.pin_code.presentation.settings_main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.core.dialog.standard.fakeStandardDialogControl

class FakePinCodeSettingsMainComponent : PinCodeSettingsMainComponent {
    override val isPinCodeEnabled: StateFlow<Boolean> = MutableStateFlow(false)
    override val isBiometricEnabled: StateFlow<Boolean> = MutableStateFlow(false)
    override val isBiometricSwitchVisible = MutableStateFlow(false)
    override val biometricDialogControl: StandardDialogControl = fakeStandardDialogControl()

    override fun onPinCodeEnabledChanged(isEnabled: Boolean) = Unit

    override fun onBiometricEnabledChanged(isEnabled: Boolean) = Unit
}