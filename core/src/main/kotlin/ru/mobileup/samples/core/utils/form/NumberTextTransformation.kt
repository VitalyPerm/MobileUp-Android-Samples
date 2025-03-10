package ru.mobileup.samples.core.utils.form

import ru.mobileup.kmm_form_validation.options.TextTransformation

object NumberTextTransformation : TextTransformation {

    override fun transform(text: String) = text.filter(Char::isDigit)
}