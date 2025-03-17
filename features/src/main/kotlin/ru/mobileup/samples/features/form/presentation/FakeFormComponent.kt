package ru.mobileup.samples.features.form.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.utils.fakeCheckControl
import ru.mobileup.samples.core.utils.fakeInputControl

class FakeFormComponent : FormComponent {

    override val phoneInputControl = fakeInputControl()

    override val passwordInputControl = fakeInputControl()

    override val agreementWithTermsCheckControl = fakeCheckControl()

    override val isLoginEnabled = MutableStateFlow(true)

    override val isLoginInProgress = MutableStateFlow(false)

    override fun onLoginClick() = Unit

    override fun onAgreementClick(tag: String) = Unit
}
