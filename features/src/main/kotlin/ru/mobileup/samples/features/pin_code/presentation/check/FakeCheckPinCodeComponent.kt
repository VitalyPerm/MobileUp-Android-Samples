package ru.mobileup.samples.features.pin_code.presentation.check

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.dialog.standard.StandardDialogControl
import ru.mobileup.samples.core.dialog.standard.fakeStandardDialogControl
import ru.mobileup.samples.features.pin_code.domain.PinCodeProgressState

class FakeCheckPinCodeComponent : CheckPinCodeComponent {
    override val pinProgressState = MutableStateFlow<PinCodeProgressState>(PinCodeProgressState.Success)
    override val isErrorText = MutableStateFlow(false)
    override val dialogControl: StandardDialogControl = fakeStandardDialogControl()
    override val endButtonState = MutableStateFlow(CheckPinCodeComponent.EndButtonState.Erase)

    override fun onDigitClick(digit: Int) = Unit
    override fun onEraseClick() = Unit
    override fun onPinCodeInputAnimationEnd() = Unit
    override fun onBiometricClick() = Unit
    override fun onLogoutClick() = Unit
}
