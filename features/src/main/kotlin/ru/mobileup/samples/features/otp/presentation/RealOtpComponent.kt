package ru.mobileup.samples.features.otp.presentation

import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.samples.core.error_handling.ErrorHandler
import ru.mobileup.samples.core.error_handling.safeLaunch
import ru.mobileup.samples.core.timer.Timer
import ru.mobileup.samples.core.timer.isTicking
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.core.utils.computed
import ru.mobileup.samples.core.utils.numberInputControl
import ru.mobileup.samples.core.utils.withProgress
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.otp.sms_retriever.SmsRetrievingResult

class RealOtpComponent(
    componentContext: ComponentContext,
    private val errorHandler: ErrorHandler,
    private val onOutput: (OtpComponent.Output) -> Unit
) : ComponentContext by componentContext, OtpComponent {

    companion object {
        private const val RESEND_CODE_AGAIN_TIMER_LOCK = 10L
        private const val CONFIRMATION_CODE_LENGTH = 4
        private const val SUCCESSFULLY_ANIMATION_DELAY = 1000L
    }

    override val confirmationCodeInputControl = numberInputControl(
        maxLength = CONFIRMATION_CODE_LENGTH,
        imeAction = ImeAction.None
    )

    private val timer = Timer(componentScope)

    override val timerState = timer.timerState

    override val isConfirmCodeCorrect = MutableStateFlow(false)

    override val isCodeResendInProgress = MutableStateFlow(false)

    override val isConfirmationInProgress = MutableStateFlow(false)

    override val sendCodeEnable = computed(
        timerState,
        isConfirmCodeCorrect,
        isConfirmationInProgress
    ) { timerState, isCodeSendingInProgress, isConfirmationInProgress ->
        !timerState.isTicking() && !isCodeSendingInProgress && !isConfirmationInProgress
    }

    private val codeLengthIsCorrect = confirmationCodeInputControl
        .value
        .map { it.length == CONFIRMATION_CODE_LENGTH }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        timer.start(RESEND_CODE_AGAIN_TIMER_LOCK)

        // Чтобы убрать ошибку после изменения введенного кода
        confirmationCodeInputControl.value
            .onEach { confirmationCodeInputControl.error.value = null }
            .launchIn(componentScope)

        computed(
            codeLengthIsCorrect,
            confirmationCodeInputControl.error
        ) { lengthIsCorrect, error ->
            lengthIsCorrect && error == null
        }.onEach { isValid ->
            if (isValid) {
                sendCode()
            }
        }.launchIn(componentScope)

        computed(
            isCodeResendInProgress,
            isConfirmationInProgress,
            isConfirmCodeCorrect
        ) { isCodeSendingInProgress, isConfirmationInProgress, isCodeCorrect ->
            isCodeSendingInProgress || isConfirmationInProgress || isCodeCorrect
        }.onEach { enterFieldDisable ->
            confirmationCodeInputControl.enabled.value = !enterFieldDisable
        }.launchIn(componentScope)

        confirmationCodeInputControl.onFocusChange(true)
    }

    private fun sendCode() {
        componentScope.safeLaunch(errorHandler) {
            withProgress(isConfirmationInProgress) {
                delay(2000)
                if (confirmationCodeInputControl.value.value == "1234") {
                    isConfirmCodeCorrect.value = true
                    delay(SUCCESSFULLY_ANIMATION_DELAY)
                    onOutput(OtpComponent.Output.OtpSuccessfullyVerified)
                } else {
                    confirmationCodeInputControl.onValueChange("")
                    confirmationCodeInputControl.error.value =
                        R.string.otp_error_code.strResDesc()
                    confirmationCodeInputControl.onFocusChange(true)
                }
            }
        }
    }

    override fun onResendCodeClick() {
        componentScope.safeLaunch(errorHandler) {
            withProgress(isCodeResendInProgress) {
                delay(2000)
                timer.start(RESEND_CODE_AGAIN_TIMER_LOCK)
                confirmationCodeInputControl.onFocusChange(true)
            }
        }
    }

    override fun onSmsCodeRetrieved(result: SmsRetrievingResult) {
        if (result is SmsRetrievingResult.Success) {
            confirmationCodeInputControl.onValueChange(result.otpCode)
        }
    }
}