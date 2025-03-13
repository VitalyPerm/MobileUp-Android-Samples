package ru.mobileup.samples.features.otp.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.samples.core.timer.TimerState

interface OtpComponent {
    val confirmationCodeInputControl: InputControl
    val timerState: StateFlow<TimerState>
    val sendCodeEnable: StateFlow<Boolean>
    val isConfirmCodeCorrect: StateFlow<Boolean>
    val isCodeResendInProgress: StateFlow<Boolean>
    val isConfirmationInProgress: StateFlow<Boolean>

    fun onResendCodeClick()

    sealed interface Output {
        data object OtpSuccessfullyVerified : Output
    }
}