package ru.mobileup.samples.features.otp.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.timer.TimerState
import ru.mobileup.samples.core.timer.isTicking
import ru.mobileup.samples.core.timer.timerFormat
import ru.mobileup.samples.core.utils.DisableTextFieldContextMenu
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.core.utils.ResourceFormatted
import ru.mobileup.samples.core.utils.clickableNoRipple
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.core.widget.text_field.OtpTextField
import ru.mobileup.samples.features.R

@Composable
fun OtpUi(
    component: OtpComponent,
    modifier: Modifier = Modifier
) {

    val isConfirmCodeCorrect by component.isConfirmCodeCorrect.collectAsState()
    val timerState = component.timerState.collectAsState()
    val sendCodeEnable by component.sendCodeEnable.collectAsState()
    val isConfirmationInProgress by component.isConfirmationInProgress.collectAsState()
    val isCodeResendInProgress by component.isCodeResendInProgress.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.otp_caption_email),
            color = CustomTheme.colors.text.secondary,
            modifier = Modifier
                .padding(top = 24.dp),
        )

        DisableTextFieldContextMenu {
            OtpTextField(
                inputControl = component.confirmationCodeInputControl,
                isCorrectCode = isConfirmCodeCorrect,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
            )
        }

        SendCodeAgainButton(
            timerState = timerState,
            sendCodeEnable = sendCodeEnable,
            isCodeResendInProgress = isCodeResendInProgress,
            onResendCodeClick = component::onResendCodeClick
        )

        if (isConfirmationInProgress) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 10.dp)
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = CustomTheme.colors.button.primary,
                    strokeWidth = 5.dp,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        if (isConfirmCodeCorrect) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 10.dp)
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                AppButton(
                    buttonType = ButtonType.Secondary,
                    onClick = component::resetState,
                    text = stringResource(R.string.otp_reset_state)
                )
            }
        }
    }
}

@Composable
private fun SendCodeAgainButton(
    timerState: State<TimerState>,
    sendCodeEnable: Boolean,
    isCodeResendInProgress: Boolean,
    onResendCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sendCodeText by remember(timerState.value) {
        mutableStateOf(
            when {
                timerState.value.isTicking() -> {
                    StringDesc.ResourceFormatted(
                        resourceId = R.string.otp_send_again_after_time_caption,
                        args = listOf(timerState.value.timerFormat())
                    )
                }
                else -> StringDesc.Resource(R.string.otp_send_again_caption)
            }
        )
    }

    val sendCodeColorText by animateColorAsState(
        targetValue = if (sendCodeEnable) {
            CustomTheme.colors.text.primary
        } else {
            CustomTheme.colors.text.secondary
        },
        label = "animated send code text color"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = sendCodeText.localized(),
            modifier = Modifier
                .padding(vertical = 28.dp)
                .clickableNoRipple(
                    enabled = sendCodeEnable,
                    onResendCodeClick
                )
                .animateContentSize(),
            style = CustomTheme.typography.button.bold,
            color = sendCodeColorText,
        )

        if (isCodeResendInProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = CustomTheme.colors.button.primary,
                strokeWidth = 2.dp,
                strokeCap = StrokeCap.Round
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun OtpUiPreview() {
    AppTheme {
        OtpUi(FakeOtpComponent())
    }
}