package ru.mobileup.samples.features.form.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.kmm_form_validation.options.VisualTransformation
import ru.mobileup.samples.core.message.presentation.noOverlapByMessage
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.systemBarsWithImePadding
import ru.mobileup.samples.core.widget.TextWithLinks
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.core.widget.checkbox.AppCheckbox
import ru.mobileup.samples.core.widget.text_field.AppTextField
import ru.mobileup.samples.features.R

@Composable
fun FormUi(
    component: FormComponent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsWithImePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val phone by component.phoneInputControl.text.collectAsState()
            val phoneHasFocus by component.phoneInputControl.hasFocus.collectAsState()

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = component.phoneInputControl,
                placeholder = stringResource(R.string.form_phone_placeholder),
                visualTransformation = VisualTransformation.None.takeIf { phone.isEmpty() && !phoneHasFocus }
            )

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = component.passwordInputControl,
                placeholder = stringResource(R.string.form_password_placeholder),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppCheckbox(
                    modifier = Modifier.align(Alignment.Top),
                    checkControl = component.agreementWithTermsCheckControl,
                )
                TextWithLinks(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.form_policy),
                    annotationsTags = FormComponent.agreementTags,
                    onLinkClick = component::onAgreementClick
                )
            }
        }

        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            val isLoginEnabled by component.isLoginEnabled.collectAsState()
            val isLoginInProgress by component.isLoginInProgress.collectAsState()

            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .noOverlapByMessage(),
                buttonType = ButtonType.Primary,
                text = stringResource(R.string.form_login),
                isEnabled = isLoginEnabled,
                isLoading = isLoginInProgress,
                onClick = { component.onLoginClick() }
            )
        }
    }
}

@Preview
@Composable
private fun FormUiPreview() {
    AppTheme {
        FormUi(FakeFormComponent())
    }
}