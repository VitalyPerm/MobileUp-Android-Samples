package ru.mobileup.samples.features.form.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.kmm_form_validation.options.VisualTransformation
import ru.mobileup.samples.core.message.presentation.noOverlapByMessage
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.utils.systemBarsWithImePadding
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.core.widget.text_field.AppTextField
import ru.mobileup.samples.features.R
import ru.mobileup.samples.core.R as CoreR

@Composable
fun FormUi(
    component: FormComponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsWithImePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val phone by component.phoneInputControl.text.collectAsState()
            val phoneHasFocus by component.phoneInputControl.hasFocus.collectAsState()

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                inputControl = component.phoneInputControl,
                placeholder = stringResource(R.string.form_phone_placeholder),
                visualTransformation = VisualTransformation.None.takeIf { phone.isEmpty() && !phoneHasFocus }
            )

            var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                inputControl = component.passwordInputControl,
                placeholder = stringResource(R.string.form_password_placeholder),
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { isPasswordVisible = !isPasswordVisible }
                            .padding(8.dp),
                        painter = if (isPasswordVisible) {
                            painterResource(CoreR.drawable.ic_24_eye_on)
                        } else {
                            painterResource(CoreR.drawable.ic_24_eye_off)
                        },
                        contentDescription = null
                    )
                },
                visualTransformation = VisualTransformation.None.takeIf { isPasswordVisible }
            )
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