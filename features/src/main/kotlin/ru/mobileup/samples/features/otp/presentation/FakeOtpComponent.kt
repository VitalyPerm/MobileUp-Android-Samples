package ru.mobileup.samples.features.otp.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.timer.TimerState
import ru.mobileup.samples.core.widget.text_field.otpFakeInputControl
import ru.mobileup.samples.features.otp.sms_retriever.SmsRetrievingResult

class FakeOtpComponent : OtpComponent {
    override val confirmationCodeInputControl = otpFakeInputControl()
    override val timerState = MutableStateFlow(TimerState.Idle)
    override val sendCodeEnable = MutableStateFlow(true)
    override val isConfirmCodeCorrect = MutableStateFlow(false)
    override val isCodeResendInProgress = MutableStateFlow(false)
    override val isConfirmationInProgress = MutableStateFlow(false)
    override fun onResendCodeClick() = Unit
    override fun onSmsCodeRetrieved(result: SmsRetrievingResult) = Unit
}