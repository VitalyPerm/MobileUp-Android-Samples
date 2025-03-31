package ru.mobileup.samples.features.pin_code.presentation.check

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.features.pin_code.domain.PinCodeProgressState

interface CheckPinCodeComponent {
    val pinProgressState: StateFlow<PinCodeProgressState>
    val isBiometricsSupported: StateFlow<Boolean>
    val isError: StateFlow<Boolean>

    val dialogControl: StandardDialogControl

    val endButtonState: StateFlow<EndButtonState>

    fun onDigitClick(digit: Int)

    fun onEraseClick()

    fun onPinCodeInputAnimationEnd()

    fun onBiometricClick()

    fun onLogoutClick()

    sealed interface Output {
        data object CheckSucceeded : Output
        data object LoggedOut : Output
    }

    enum class EndButtonState {
        Erase, Biometrics, None
    }
}
