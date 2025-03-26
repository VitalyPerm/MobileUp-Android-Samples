package ru.mobileup.samples.core.utils

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import ru.mobileup.kmm_form_validation.control.CheckControl
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.options.KeyboardOptions
import ru.mobileup.kmm_form_validation.options.KeyboardType
import ru.mobileup.kmm_form_validation.options.TextTransformation
import ru.mobileup.kmm_form_validation.options.VisualTransformation
import ru.mobileup.kmm_form_validation.validation.form.FormValidator
import ru.mobileup.kmm_form_validation.validation.form.FormValidatorBuilder
import ru.mobileup.kmm_form_validation.validation.form.formValidator
import ru.mobileup.samples.core.utils.form.NumberTextTransformation

fun ComponentContext.InputControl(
    initialText: String = "",
    singleLine: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textTransformation: TextTransformation? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
): InputControl = InputControl(
    componentScope,
    initialText,
    singleLine,
    maxLength,
    keyboardOptions,
    textTransformation,
    visualTransformation
)

fun ComponentContext.numberInputControl(
    maxLength: Int = Int.MAX_VALUE,
    imeAction: ImeAction = ImeAction.Next,
): InputControl = InputControl(
    coroutineScope = componentScope,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = imeAction
    ),
    maxLength = maxLength,
    textTransformation = NumberTextTransformation
)

fun ComponentContext.CheckControl(
    initialChecked: Boolean = false
): CheckControl = CheckControl(componentScope, initialChecked)

fun ComponentContext.formValidator(
    buildBlock: FormValidatorBuilder.() -> Unit
): FormValidator = componentScope.formValidator(buildBlock)

@OptIn(DelicateCoroutinesApi::class)
fun fakeInputControl() = InputControl(GlobalScope)

@OptIn(DelicateCoroutinesApi::class)
fun fakeCheckControl() = CheckControl(GlobalScope)