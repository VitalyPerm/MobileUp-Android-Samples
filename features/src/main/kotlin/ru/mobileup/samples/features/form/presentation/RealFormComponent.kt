package ru.mobileup.samples.features.form.presentation

import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.strResDesc
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.options.KeyboardOptions
import ru.mobileup.kmm_form_validation.options.KeyboardType
import ru.mobileup.kmm_form_validation.validation.control.isNotBlank
import ru.mobileup.kmm_form_validation.validation.control.validation
import ru.mobileup.kmm_form_validation.validation.form.FormValidator
import ru.mobileup.kmm_form_validation.validation.form.RevalidateOnValueChanged
import ru.mobileup.kmm_form_validation.validation.form.SetFocusOnFirstInvalidControlAfterValidation
import ru.mobileup.kmm_form_validation.validation.form.ValidateOnFocusLost
import ru.mobileup.samples.core.utils.InputControl
import ru.mobileup.samples.core.utils.formValidator
import ru.mobileup.samples.core.R as CoreR

private const val PHONE_PREFIX_DIGIT = "7"
private const val PHONE_DIGIT_COUNT_WITHOUT_PREFIX = 10 // 7 XXX XXX XX XX

class RealFormComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, FormComponent {

    override val phoneInputControl = InputControl(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        textTransformation = { text -> text.filter { it.isDigit() } },
        maxLength = PHONE_DIGIT_COUNT_WITHOUT_PREFIX
    )

    private val formValidator: FormValidator = formValidator {
        features = listOf(
            ValidateOnFocusLost,
            RevalidateOnValueChanged,
            SetFocusOnFirstInvalidControlAfterValidation
        )

        input(phoneInputControl) {
            isNotBlank(CoreR.string.field_error_is_blank.strResDesc())

            validation(
                { it.length == PHONE_DIGIT_COUNT_WITHOUT_PREFIX },
                CoreR.string.field_error_invalid_format.strResDesc()
            )
        }
    }
}