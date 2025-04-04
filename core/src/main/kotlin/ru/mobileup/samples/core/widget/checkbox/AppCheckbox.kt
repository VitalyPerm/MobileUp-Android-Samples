package ru.mobileup.samples.core.widget.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.mobileup.kmm_form_validation.control.CheckControl
import ru.mobileup.samples.core.theme.AppTheme

@Composable
fun AppCheckbox(
    checkControl: CheckControl,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CheckboxColors = AppCheckboxDefaults.colors,
) {
    val value by checkControl.value.collectAsState()
    val enabled by checkControl.enabled.collectAsState()
    AppCheckbox(
        isChecked = value,
        onCheckedChange = checkControl::onValueChange,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = colors
    )
}

@Composable
fun AppCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CheckboxColors = AppCheckboxDefaults.colors,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Checkbox(
            modifier = modifier,
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            interactionSource = interactionSource,
            colors = colors
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppCheckboxPreview() {
    AppTheme {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppCheckbox(
                isChecked = true,
                onCheckedChange = {},
            )
            AppCheckbox(
                isChecked = false,
                onCheckedChange = {},
            )
        }
    }
}