package ru.mobileup.samples.features.form.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.kmm_form_validation.options.VisualTransformation
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.PhoneNumberVisualTransformation
import ru.mobileup.samples.core.widget.text_field.AppTextField
import ru.mobileup.samples.features.R

@Composable
fun FormUi(
    component: FormComponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        val phone by component.phoneInputControl.text.collectAsState()
        val phoneHasFocus by component.phoneInputControl.hasFocus.collectAsState()

        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            inputControl = component.phoneInputControl,
            placeholder = stringResource(R.string.form_phone_placeholder),
            visualTransformation = if (phone.isNotEmpty() || phoneHasFocus) {
                PhoneNumberVisualTransformation
            } else {
                VisualTransformation.None
            }
        )
    }
}

@Preview
@Composable
private fun FormUiPreview() {
    AppTheme {
        FormUi(FakeFormComponent())
    }
}