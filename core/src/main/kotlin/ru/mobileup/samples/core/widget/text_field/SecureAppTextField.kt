package ru.mobileup.samples.core.widget.text_field

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.kmm_form_validation.toCompose
import ru.mobileup.samples.core.R
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.isKeyboardVisibleAsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SecureAppTextField(
    inputControl: InputControl,
    modifier: Modifier = Modifier,
    shape: Shape = AppTextFieldDefaults.shape,
    textStyle: TextStyle = AppTextFieldDefaults.textStyle,
    labelStyle: TextStyle = AppTextFieldDefaults.labelStyle,
    colors: TextFieldColors = AppTextFieldDefaults.colors,
    leadingIcon: @Composable (() -> Unit)? = null,
    border: BorderStroke? = null,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    headerText: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    @SuppressLint("StateFlowValueCalledInComposition")
    val state = rememberTextFieldState(initialText = inputControl.text.value)

    LaunchedEffect(Unit) {
        snapshotFlow(state::text)
            .map(CharSequence::toString)
            .collect(inputControl::onTextChanged)
    }

    val hasFocus by inputControl.hasFocus.collectAsState()
    val error by inputControl.error.collectAsState()
    val enabled by inputControl.enabled.collectAsState()
    val isKeyboardVisible by isKeyboardVisibleAsState()

    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(Unit) {
        inputControl.scrollToItEvent.collectLatest {
            bringIntoViewRequester.bringIntoView()
        }
    }

    LaunchedEffect(hasFocus, isKeyboardVisible) {
        if (hasFocus && isKeyboardVisible) {
            focusRequester.requestFocus()
            delay(30) // Wait for the keyboard to fully open before bringing the text field into view
            bringIntoViewRequester.bringIntoView()
        }
    }

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .animateContentSize()
            .bringIntoViewRequester(bringIntoViewRequester),
    ) {
        headerText?.let {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = it,
                color = CustomTheme.colors.text.primary,
                style = CustomTheme.typography.title.regular
            )
        }

        SecureTextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = border ?: AppTextFieldDefaults.border(error != null, hasFocus),
                    shape = shape
                )
                .focusRequester(focusRequester)
                .onFocusChanged { inputControl.onFocusChanged(it.isFocused) },
            keyboardOptions = remember { inputControl.keyboardOptions.toCompose() },
            state = state,
            enabled = enabled,
            isError = error != null,
            textObfuscationMode = if (isPasswordVisible) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.RevealLastTyped
            },
            trailingIcon = {
                Crossfade(isPasswordVisible) { visible ->
                    Icon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { isPasswordVisible = !isPasswordVisible }
                            .padding(8.dp),
                        painter = painterResource(
                            if (visible) R.drawable.ic_24_eye_on else R.drawable.ic_24_eye_off
                        ),
                        contentDescription = null
                    )
                }
            },
            label = label?.let {
                {
                    Text(text = it, style = labelStyle)
                }
            },
            placeholder = placeholder?.let {
                {
                    Text(text = it, style = textStyle)
                }
            },
            leadingIcon = leadingIcon,
            colors = colors,
            shape = shape,
            interactionSource = interactionSource
        )

        AnimatedContent(
            targetState = error?.localized() ?: supportingText,
            transitionSpec = {
                slideInVertically { -it } togetherWith slideOutVertically { it }
            }
        ) { textToDisplay ->
            if (!textToDisplay.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    text = textToDisplay,
                    color = if (textToDisplay == error?.localized()) {
                        CustomTheme.colors.text.error
                    } else {
                        CustomTheme.colors.text.primary
                    },
                    style = CustomTheme.typography.caption.regular
                )
            }
        }
    }
}
