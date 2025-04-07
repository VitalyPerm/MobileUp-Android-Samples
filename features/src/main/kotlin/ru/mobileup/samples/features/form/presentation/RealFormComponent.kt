package ru.mobileup.samples.features.form.presentation

import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mobileup.kmm_form_validation.control.CheckControl
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.options.KeyboardOptions
import ru.mobileup.kmm_form_validation.options.KeyboardType
import ru.mobileup.kmm_form_validation.validation.control.isNotBlank
import ru.mobileup.kmm_form_validation.validation.control.validation
import ru.mobileup.kmm_form_validation.validation.form.FormValidator
import ru.mobileup.kmm_form_validation.validation.form.RevalidateOnValueChanged
import ru.mobileup.kmm_form_validation.validation.form.SetFocusOnFirstInvalidControlAfterValidation
import ru.mobileup.kmm_form_validation.validation.form.ValidateOnFocusLost
import ru.mobileup.kmm_form_validation.validation.form.checked
import ru.mobileup.samples.core.error_handling.ErrorHandler
import ru.mobileup.samples.core.error_handling.safeLaunch
import ru.mobileup.samples.core.error_handling.safeRun
import ru.mobileup.samples.core.external_apps.data.ExternalAppService
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.utils.CheckControl
import ru.mobileup.samples.core.utils.InputControl
import ru.mobileup.samples.core.utils.PhoneNumberVisualTransformation
import ru.mobileup.samples.core.utils.ResourceFormatted
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.core.utils.computed
import ru.mobileup.samples.core.utils.formValidator
import ru.mobileup.samples.core.utils.withProgress
import ru.mobileup.samples.features.R
import ru.mobileup.samples.core.R as CoreR

private const val PHONE_PREFIX_DIGIT = "7"
private const val PHONE_DIGIT_COUNT_WITHOUT_PREFIX = 10 // 7 XXX XXX XX XX
private const val PASSWORD_SPEC_CHARS = "!@#$%^&*_-+"
private val PASSWORD_RANGE = 8..20

class RealFormComponent(
    componentContext: ComponentContext,
    private val externalAppService: ExternalAppService,
    private val errorHandler: ErrorHandler,
    private val messageService: MessageService,
) : ComponentContext by componentContext, FormComponent {

    override val phoneInputControl = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        textTransformation = { text -> text.filter { it.isDigit() } },
        visualTransformation = PhoneNumberVisualTransformation,
        maxLength = PHONE_DIGIT_COUNT_WITHOUT_PREFIX
    )

    override val passwordInputControl = InputControl(
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
    )

    override val agreementWithTermsCheckControl: CheckControl = CheckControl()

    private val formValidator: FormValidator = formValidator {
        features = listOf(
            ValidateOnFocusLost,
            RevalidateOnValueChanged,
            SetFocusOnFirstInvalidControlAfterValidation
        )

        input(phoneInputControl) {
            isNotBlank(CoreR.string.field_error_is_blank.strResDesc())

            validation(CoreR.string.field_error_invalid_format.strResDesc()) {
                it.length == PHONE_DIGIT_COUNT_WITHOUT_PREFIX
            }
        }

        input(passwordInputControl) {
            isNotBlank(CoreR.string.field_error_is_blank.strResDesc())

            validation(
                isValid = ::isPasswordValid,
                errorMessage = StringDesc.ResourceFormatted(
                    R.string.form_password_error,
                    PASSWORD_RANGE.first,
                    PASSWORD_RANGE.last,
                    PASSWORD_SPEC_CHARS
                )
            )
        }

        checked(agreementWithTermsCheckControl, CoreR.string.checkbox_error_terms.strResDesc())
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length !in PASSWORD_RANGE) return false

        var containsDigit = false
        var containsLowercase = false
        var containsUppercase = false
        var containsSpecChar = false

        for (char in password) {
            when {
                char.isDigit() -> containsDigit = true
                char.isLowerCase() -> containsLowercase = true
                char.isUpperCase() -> containsUppercase = true
                char in PASSWORD_SPEC_CHARS -> containsSpecChar = true
                !char.isLetterOrDigit() -> return false
            }
        }

        return containsDigit && containsLowercase && containsUppercase && containsSpecChar
    }

    override val isLoginEnabled = computed(
        formValidator.isFilledState,
        formValidator.hasErrorState,
    ) { isFilled, hasError ->
        isFilled && !hasError
    }

    override val isLoginInProgress = MutableStateFlow(false)

    init {
        isLoginInProgress
            .onEach {
                agreementWithTermsCheckControl.enabled.value = !it
                phoneInputControl.enabled.value = !it
                passwordInputControl.enabled.value = !it
            }
            .launchIn(componentScope)
    }

    override fun onLoginClick() {
        if (isLoginInProgress.value) return
        if (formValidator.validate().isInvalid) return

        componentScope.safeLaunch(errorHandler) {
            withProgress(isLoginInProgress) {
                val phone = PHONE_PREFIX_DIGIT + phoneInputControl.value.value
                val password = passwordInputControl.value.value
                delay(2000)
                messageService.showMessage(
                    Message(CoreR.string.common_success.strResDesc())
                )
            }
        }
    }

    override fun onAgreementClick(tag: String) = safeRun(errorHandler) {
        when (tag) {
            FormComponent.PRIVACY_POLICY -> "https://career.habr.com/companies/mobileup"
            FormComponent.TERMS_OF_USE_TAG -> "https://mobileup.ru/"
            else -> return@safeRun
        }.run(externalAppService::openUrl)
    }
}
