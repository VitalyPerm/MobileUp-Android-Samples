package ru.mobileup.samples.features.pin_code.presentation.create

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.features.pin_code.domain.PinCodeProgressState

interface CreatePinCodeComponent {

    val pinProgressState: StateFlow<PinCodeProgressState>
    val pinInputStep: StateFlow<PinInputStep>
    val isEraseButtonAvailable: StateFlow<Boolean>
    val biometricDialogControl: StandardDialogControl

    fun onDigitClick(digit: Int)
    fun onEraseClick()
    fun onPinCodeInputAnimationEnd()

    sealed interface Output {
        data object PinCodeSet : Output
    }

    enum class PinInputStep {
        None, Repeat, PreviouslyErred
    }
}
