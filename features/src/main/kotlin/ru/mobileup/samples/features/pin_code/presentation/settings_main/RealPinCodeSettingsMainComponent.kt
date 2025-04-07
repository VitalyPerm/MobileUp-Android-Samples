package ru.mobileup.samples.features.pin_code.presentation.settings_main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnStart
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.biometric.data.BiometricEnablingStorage
import ru.mobileup.samples.core.biometric.data.BiometricService
import ru.mobileup.samples.core.biometric.domain.BiometricAuthResult
import ru.mobileup.samples.core.biometric.domain.BiometricEnableStatus
import ru.mobileup.samples.core.biometric.domain.BiometricSupportStatus
import ru.mobileup.samples.core.dialog.standard.DialogButton
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.core.dialog.standard.StandardDialogData
import ru.mobileup.samples.core.dialog.standard.standardDialogControl
import ru.mobileup.samples.core.error_handling.ErrorHandler
import ru.mobileup.samples.core.error_handling.safeLaunch
import ru.mobileup.samples.core.external_apps.data.ExternalAppService
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.core.utils.computed
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.pin_code.data.PinCodeStorage

class RealPinCodeSettingsMainComponent(
    componentContext: ComponentContext,
    private val onOutput: (PinCodeSettingsMainComponent.Output) -> Unit,
    private val pinCodeStorage: PinCodeStorage,
    private val biometricEnablingStorage: BiometricEnablingStorage,
    private val biometricService: BiometricService,
    private val errorHandler: ErrorHandler,
    private val externalAppService: ExternalAppService,
    private val messageService: MessageService
) : ComponentContext by componentContext, PinCodeSettingsMainComponent {

    override val isPinCodeEnabled = MutableStateFlow(false)

    override val isBiometricEnabled = MutableStateFlow(false)

    private val isBiometricSupported =
        biometricService.getBiometricSupportStatus() != BiometricSupportStatus.NotSupported

    override val isBiometricSwitchVisible: StateFlow<Boolean> =
        computed(isPinCodeEnabled) { it && isBiometricSupported }

    override val biometricDialogControl: StandardDialogControl =
        standardDialogControl("biometricDialogControl")

    private val biometricNotEnrolledDialogData = StandardDialogData(
        title = R.string.pin_code_alert_biometric_header.strResDesc(),
        message = R.string.pin_code_alert_biometric_not_enrolled_text.strResDesc(),
        confirmButton = DialogButton(
            text = ru.mobileup.samples.core.R.string.common_yes.strResDesc(),
            action = ::onDialogSettingsClick
        ),
        dismissButton = DialogButton(
            text = ru.mobileup.samples.core.R.string.common_no.strResDesc(),
            action = ::onDialogCancelClick
        )
    )

    private val biometricDisabledDialogData = StandardDialogData(
        title = R.string.pin_code_alert_biometric_header.strResDesc(),
        message = R.string.pin_code_alert_biometric_disabled_text.strResDesc(),
        confirmButton = DialogButton(
            text = ru.mobileup.samples.core.R.string.common_yes.strResDesc(),
            action = ::onDialogBiometricEnableClick
        ),
        dismissButton = DialogButton(
            text = ru.mobileup.samples.core.R.string.common_no.strResDesc(),
            action = ::onDialogCancelClick
        )
    )

    private var isUserInBiometricSettings = false

    init {
        lifecycle.doOnStart {
            componentScope.safeLaunch(errorHandler) {
                isPinCodeEnabled.value = pinCodeStorage.getPinCode() != null
                isBiometricEnabled.value = biometricEnablingStorage.getBiometricEnableStatus() == BiometricEnableStatus.Enabled
            }
        }

        lifecycle.doOnResume {
            if (isUserInBiometricSettings) {
                isUserInBiometricSettings = false
                when (biometricService.getBiometricSupportStatus()) {
                    BiometricSupportStatus.Supported -> {
                        biometricDialogControl.show(biometricDisabledDialogData)
                    }

                    BiometricSupportStatus.NotEnrolled,
                    BiometricSupportStatus.NotSupported -> {}
                }
            }
        }
    }

    override fun onPinCodeEnabledChanged(isEnabled: Boolean) {
        if (isEnabled) {
            onOutput(PinCodeSettingsMainComponent.Output.CreatePinCodeRequested)
        } else {
            componentScope.safeLaunch(errorHandler) {
                pinCodeStorage.deletePinCode()
                biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Disabled)
                isPinCodeEnabled.value = false
                isBiometricEnabled.value = false
            }
        }
    }

    override fun onBiometricEnabledChanged(isEnabled: Boolean) {
        if (isEnabled) {
            when (biometricService.getBiometricSupportStatus()) {
                BiometricSupportStatus.Supported -> {
                    showBiometricsModalWindow()
                }

                BiometricSupportStatus.NotEnrolled -> {
                    biometricDialogControl.show(biometricNotEnrolledDialogData)
                }

                BiometricSupportStatus.NotSupported -> {}
            }
        } else {
            componentScope.safeLaunch(errorHandler) {
                biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Disabled)
                isBiometricEnabled.value = false
            }
        }
    }

    private fun onDialogBiometricEnableClick() {
        biometricDialogControl.dismiss()
        showBiometricsModalWindow()
    }

    private fun onDialogSettingsClick() {
        biometricDialogControl.dismiss()
        externalAppService.openBiometricSettings()
        isUserInBiometricSettings = true
    }

    private fun onDialogCancelClick() {
        componentScope.safeLaunch(errorHandler) {
            biometricDialogControl.dismiss()
            biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Disabled)
        }
    }

    private fun showBiometricsModalWindow() {
        biometricService.startBiometricAuth(
            title = ru.mobileup.samples.core.R.string.biometric_prompt_title.strResDesc(),
            description = ru.mobileup.samples.core.R.string.biometric_prompt_description.strResDesc(),
        ) {
            componentScope.safeLaunch(errorHandler) {
                when (it) {
                    BiometricAuthResult.Success -> {
                        biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Enabled)
                        isBiometricEnabled.value = true
                    }

                    BiometricAuthResult.Cancel -> {
                        biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Disabled)
                        isBiometricEnabled.value = false
                    }

                    BiometricAuthResult.TooManyAttempts -> {
                        biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Disabled)
                        isBiometricEnabled.value = false
                        messageService.showMessage(
                            Message(R.string.biometric_too_many_attempts.strResDesc())
                        )
                    }

                    BiometricAuthResult.Failed -> {}
                }
            }
        }
    }
}