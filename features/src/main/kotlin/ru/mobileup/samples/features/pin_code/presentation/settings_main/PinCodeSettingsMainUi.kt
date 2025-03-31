package ru.mobileup.samples.features.pin_code.presentation.settings_main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.widget.AppToolbar
import ru.mobileup.samples.features.R

@Composable
fun PinCodeSettingsMainUi(
    component: PinCodeSettingsMainComponent,
    modifier: Modifier = Modifier
) {

    val isPinCodeEnabled by component.isPinCodeEnabled.collectAsState()
    val isBiometricEnabled by component.isBiometricEnabled.collectAsState()
    val isBiometricSwitchVisible by component.isBiometricSwitchVisible.collectAsState()
    Scaffold(
        modifier = modifier.systemBarsPadding(),
        topBar = { AppToolbar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.pin_code_settings_pin_code),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isPinCodeEnabled,
                    onCheckedChange = component::onPinCodeEnabledChanged
                )
            }

            if (isBiometricSwitchVisible) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.pin_code_settings_biometric),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = component::onBiometricEnabledChanged
                    )
                }
            }
        }
    }
}