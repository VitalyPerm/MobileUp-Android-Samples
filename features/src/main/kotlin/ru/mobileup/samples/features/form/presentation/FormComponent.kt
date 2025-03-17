package ru.mobileup.samples.features.form.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.kmm_form_validation.control.CheckControl
import ru.mobileup.kmm_form_validation.control.InputControl

interface FormComponent {

    val phoneInputControl: InputControl

    val passwordInputControl: InputControl

    val isLoginEnabled: StateFlow<Boolean>

    val isLoginInProgress: StateFlow<Boolean>

    val agreementWithTermsCheckControl: CheckControl

    fun onLoginClick()

    fun onAgreementClick(tag: String)

    companion object {
        const val TERMS_OF_USE_TAG = "terms_of_use"
        const val PRIVACY_POLICY = "privacy_policy"
        val agreementTags = listOf(TERMS_OF_USE_TAG, PRIVACY_POLICY)
    }
}
