package ru.mobileup.samples.core.widget.text_field

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mobileup.kmm_form_validation.control.InputControl
import ru.mobileup.kmm_form_validation.options.ImeAction
import ru.mobileup.kmm_form_validation.toCompose
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.widget.text_field.AppTextFieldDefaults.otpBorder
import kotlin.math.roundToInt

private const val MAX_OTP_COUNT = 10

enum class OtpTextFieldStatus {
    Neutral, Correct, Error
}

private const val SHAKE_ANIMATION_COUNT = 4
private const val SHAKE_ANIMATION_OFFSET = 20
private const val SHAKE_ANIMATION_DURATION = 400

@Composable
fun OtpTextField(
    inputControl: InputControl,
    isCorrectCode: Boolean,
    modifier: Modifier = Modifier,
) {

    require(inputControl.maxLength <= MAX_OTP_COUNT) { "Incorrect otpCount" }

    val hasFocus by inputControl.hasFocus.collectAsState()
    val error by inputControl.error.collectAsState()
    val enabled by inputControl.enabled.collectAsState()
    val text by inputControl.value.collectAsState()
    val textFieldStatus by remember(isCorrectCode, error) {
        mutableStateOf(
            when {
                isCorrectCode -> OtpTextFieldStatus.Correct
                error != null -> OtpTextFieldStatus.Error
                else -> OtpTextFieldStatus.Neutral
            }
        )
    }

    OtpTextField(
        modifier = modifier,
        text = text,
        otpCount = inputControl.maxLength,
        textFieldStatus = textFieldStatus,
        isEnabled = enabled,
        onTextChange = inputControl::onValueChange,
        onFocusChange = inputControl::onFocusChange,
        hasFocus = hasFocus,
        keyboardOptions = inputControl.keyboardOptions.toCompose()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OtpTextField(
    text: String,
    onTextChange: (String) -> Unit,
    textFieldStatus: OtpTextFieldStatus,
    otpCount: Int,
    modifier: Modifier = Modifier,
    onFocusChange: (Boolean) -> Unit = {},
    isEnabled: Boolean = true,
    hasFocus: Boolean = false,
    shape: Shape = AppTextFieldDefaults.otpShape,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val focusRequester = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val coroutineScope = rememberCoroutineScope()

    val currentValue by rememberUpdatedState(text)

    val keyboardController = LocalSoftwareKeyboardController.current

    val currentTextFieldValue by remember(currentValue) {
        mutableStateOf(
            TextFieldValue(currentValue, selection = TextRange(currentValue.length))
        )
    }

    val shakeAnimation = remember { Animatable(0f) }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(textFieldStatus) {
        if (textFieldStatus == OtpTextFieldStatus.Error) {
            for (i in 0 until SHAKE_ANIMATION_COUNT) {
                val currentAmplitude = (1 - i / SHAKE_ANIMATION_COUNT.toFloat())
                shakeAnimation.animateTo(
                    targetValue = currentAmplitude,
                    animationSpec = tween(
                        durationMillis = SHAKE_ANIMATION_DURATION / (SHAKE_ANIMATION_COUNT * 2),
                        easing = LinearEasing
                    )
                )
                shakeAnimation.animateTo(
                    targetValue = -currentAmplitude,
                    animationSpec = tween(
                        durationMillis = SHAKE_ANIMATION_DURATION / (SHAKE_ANIMATION_COUNT * 2),
                        easing = LinearEasing
                    )
                )
                shakeAnimation.snapTo(0f)
            }
        } else {
            shakeAnimation.snapTo(0f)
        }
    }

    BasicTextField(
        modifier = modifier
            .offset {
                IntOffset(
                    x = (shakeAnimation.value * SHAKE_ANIMATION_OFFSET).roundToInt(),
                    y = 0
                )
            }
            .bringIntoViewRequester(bringIntoViewRequester)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    keyboardController?.show()
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                } else {
                    keyboardController?.hide()
                }
                onFocusChange(focusState.isFocused)
            },
        value = currentTextFieldValue,
        onValueChange = { onTextChange(it.text) },
        enabled = isEnabled,
        readOnly = !isEnabled,
        keyboardActions = AppTextFieldDefaults.defaultKeyboardActions,
        keyboardOptions = keyboardOptions,
        singleLine = true,
    ) { _ ->
        DecorationBox(
            otpCount = otpCount,
            text = text,
            hasFocus = hasFocus,
            textFieldStatus = textFieldStatus,
            shape = shape
        )
    }
}

@Composable
private fun DecorationBox(
    otpCount: Int,
    text: String,
    hasFocus: Boolean,
    textFieldStatus: OtpTextFieldStatus,
    shape: Shape
) {

    Box(contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(otpCount) { index ->
                key(index) {
                    val char by remember(text) {
                        mutableStateOf(text.getOrNull(index)?.toString() ?: "")
                    }
                    val isInFocus by remember(hasFocus, text.length) {
                        mutableStateOf(hasFocus && index == text.length)
                    }

                    OtpItem(
                        char = char,
                        isInFocus = isInFocus,
                        textFieldStatus = textFieldStatus,
                        shape = shape,
                    )
                }
            }
        }
    }
}

@Composable
private fun OtpItem(
    char: String,
    isInFocus: Boolean,
    textFieldStatus: OtpTextFieldStatus,
    shape: Shape,
    modifier: Modifier = Modifier
) {

    val border = otpBorder(
        textFieldStatus = textFieldStatus,
        hasFocus = isInFocus
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .background(CustomTheme.colors.textField.background, shape)
            .drawWithContent {
                drawContent()
                val path = Path().apply {
                    addOutline(shape.createOutline(size, layoutDirection, this@drawWithContent))
                }
                drawPath(
                    path = path,
                    brush = border.value.brush,
                    style = Stroke(width = border.value.width.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = CustomTheme.typography.body.regular.copy(fontWeight = FontWeight.Bold),
            color = CustomTheme.colors.text.primary,
            textAlign = TextAlign.Center
        )

        if (isInFocus) {
            val infiniteTransition = rememberInfiniteTransition(label = "")

            val cursorAlpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    repeatMode = RepeatMode.Reverse,
                    animation = keyframes { durationMillis = AppTextFieldDefaults.CURSOR_ANIMATION_DURATION }
                ),
                label = "cursorAlpha"
            )

            Spacer(
                modifier = Modifier
                    .graphicsLayer { alpha = cursorAlpha }
                    .size(width = 2.dp, height = 22.dp)
                    .background(CustomTheme.colors.text.primary)
            )
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun otpFakeInputControl() = InputControl(
    GlobalScope,
    keyboardOptions = ru.mobileup.kmm_form_validation.options.KeyboardOptions(
        keyboardType = ru.mobileup.kmm_form_validation.options.KeyboardType.Number,
        imeAction = ImeAction.None
    ),
    maxLength = 4
)

@Preview
@Composable
private fun OtpTextFieldPreview() {
    AppTheme {
        OtpTextField(
            inputControl = otpFakeInputControl(),
            isCorrectCode = false
        )
    }
}