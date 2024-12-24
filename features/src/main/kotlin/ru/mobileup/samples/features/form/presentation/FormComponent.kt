package ru.mobileup.samples.features.form.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.kmm_form_validation.control.InputControl

interface FormComponent {

    val phoneInputControl: InputControl

    val passwordInputControl: InputControl

    val isLoginEnabled: StateFlow<Boolean>

    val isLoginInProgress: StateFlow<Boolean>

    fun onLoginClick()
}
