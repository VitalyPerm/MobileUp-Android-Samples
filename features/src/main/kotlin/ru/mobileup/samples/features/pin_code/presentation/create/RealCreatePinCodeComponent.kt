package ru.mobileup.samples.features.pin_code.presentation.create

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
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
import ru.mobileup.samples.features.pin_code.domain.PinCode
import ru.mobileup.samples.features.pin_code.domain.PinCodeProgressState
import ru.mobileup.samples.core.R as CoreR

class RealCreatePinCodeComponent(
    componentContext: ComponentContext,
    private val pinCodeStorage: PinCodeStorage,
    private val biometricService: BiometricService,
    private val biometricEnablingStorage: BiometricEnablingStorage,
    private val externalAppService: ExternalAppService,
    private val messageService: MessageService,
    private val errorHandler: ErrorHandler,
    private val onOutput: (CreatePinCodeComponent.Output) -> Unit,
) : ComponentContext by componentContext, CreatePinCodeComponent {

    override val pinProgressState = MutableStateFlow<PinCodeProgressState>(
        PinCodeProgressState.Progress(0)
    )

    override val pinInputStep = MutableStateFlow(
        CreatePinCodeComponent.PinInputStep.None
    )

    override val biometricDialogControl: StandardDialogControl =
        standardDialogControl("biometricDialogControl")

    override val isEraseButtonAvailable: StateFlow<Boolean> = computed(pinProgressState) {
        it is PinCodeProgressState.Progress && it.count > 0
    }

    private val biometricNotEnrolledDialogData = StandardDialogData(
        title = R.string.pin_code_alert_biometric_header.strResDesc(),
        message = R.string.pin_code_alert_biometric_not_enrolled_text.strResDesc(),
        confirmButton = DialogButton(
            text = CoreR.string.common_yes.strResDesc(),
            action = ::onDialogSettingsClick
        ),
        dismissButton = DialogButton(
            text = CoreR.string.common_no.strResDesc(),
            action = ::onDialogCancelClick
        )
    )

    private val biometricDisabledDialogData = StandardDialogData(
        title = R.string.pin_code_alert_biometric_header.strResDesc(),
        message = R.string.pin_code_alert_biometric_disabled_text.strResDesc(),
        confirmButton = DialogButton(
            text = CoreR.string.common_yes.strResDesc(),
            action = ::onDialogBiometricEnableClick
        ),
        dismissButton = DialogButton(
            text = CoreR.string.common_no.strResDesc(),
            action = ::onDialogCancelClick
        )
    )

    private var currentPinCode = ""
    private var currentRepeatPinCode = ""

    private var isUserInBiometricSettings = false

    init {
        lifecycle.doOnResume {
            if (isUserInBiometricSettings) {
                isUserInBiometricSettings = false
                when (biometricService.getBiometricSupportStatus()) {
                    BiometricSupportStatus.Supported -> {
                        biometricDialogControl.show(biometricDisabledDialogData)
                    }

                    BiometricSupportStatus.NotEnrolled,
                    BiometricSupportStatus.NotSupported -> {
                        goToNextScreen()
                    }
                }
            }
        }
    }

    override fun onDigitClick(digit: Int) {
        if (currentRepeatPinCode.length >= PinCode.LENGTH) return

        when (pinInputStep.value) {
            CreatePinCodeComponent.PinInputStep.Repeat -> {
                currentRepeatPinCode += digit
                if (currentRepeatPinCode.length == PinCode.LENGTH) {
                    validatePinCode()
                } else {
                    updateProgressState()
                }
            }

            CreatePinCodeComponent.PinInputStep.PreviouslyErred,
            CreatePinCodeComponent.PinInputStep.None -> {
                if (currentPinCode.length >= PinCode.LENGTH) return
                currentPinCode += digit
                updateProgressState()
            }
        }
    }

    override fun onEraseClick() {
        when (pinInputStep.value) {
            CreatePinCodeComponent.PinInputStep.Repeat -> currentRepeatPinCode =
                currentRepeatPinCode.dropLast(1)

            CreatePinCodeComponent.PinInputStep.PreviouslyErred,
            CreatePinCodeComponent.PinInputStep.None -> currentPinCode = currentPinCode.dropLast(1)
        }
        updateProgressState()
    }

    override fun onPinCodeInputAnimationEnd() {
        when (pinProgressState.value) {
            is PinCodeProgressState.Progress -> {
                pinInputStep.value = CreatePinCodeComponent.PinInputStep.Repeat
                updateProgressState()
            }

            is PinCodeProgressState.Error -> {
                pinInputStep.value = CreatePinCodeComponent.PinInputStep.PreviouslyErred
                currentRepeatPinCode = ""
                currentPinCode = ""
                updateProgressState()
            }

            is PinCodeProgressState.Success -> {
                componentScope.safeLaunch(errorHandler) {
                    val biometricEnableStatus = biometricEnablingStorage.getBiometricEnableStatus()
                    if (biometricEnableStatus != BiometricEnableStatus.Unknown) {
                        goToNextScreen()
                    } else {
                        when (biometricService.getBiometricSupportStatus()) {
                            BiometricSupportStatus.Supported -> {
                                biometricDialogControl.show(biometricDisabledDialogData)
                            }

                            BiometricSupportStatus.NotEnrolled -> {
                                biometricDialogControl.show(biometricNotEnrolledDialogData)
                            }

                            BiometricSupportStatus.NotSupported -> {
                                goToNextScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateProgressState() {
        val currentInput = if (pinInputStep.value == CreatePinCodeComponent.PinInputStep.Repeat) {
            currentRepeatPinCode
        } else {
            currentPinCode
        }
        pinProgressState.value = PinCodeProgressState.Progress(currentInput.length)
    }

    private fun validatePinCode() {
        pinProgressState.value = if (currentPinCode != currentRepeatPinCode) {
            PinCodeProgressState.Error
        } else {
            PinCodeProgressState.Success
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
            goToNextScreen()
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
                    }

                    BiometricAuthResult.TooManyAttempts -> {
                        biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Disabled)
                        messageService.showMessage(
                            Message(R.string.biometric_too_many_attempts.strResDesc())
                        )
                    }

                    BiometricAuthResult.Cancel,
                    BiometricAuthResult.Failed -> {
                        biometricEnablingStorage.putBiometricEnableStatus(BiometricEnableStatus.Unknown)
                    }
                }
                if (it != BiometricAuthResult.Failed) goToNextScreen()
            }
        }
    }

    private fun goToNextScreen() {
        componentScope.safeLaunch(errorHandler) {
            pinCodeStorage.savePinCode(PinCode(currentPinCode))
            onOutput(CreatePinCodeComponent.Output.PinCodeSet)
        }
    }
}
