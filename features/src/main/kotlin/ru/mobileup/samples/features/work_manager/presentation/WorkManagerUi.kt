package ru.mobileup.samples.features.work_manager.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.R

@Composable
fun WorkManagerUi(
    component: WorkManagerComponent,
    modifier: Modifier = Modifier,
) {
    val isReminderEnabled by component.isReminderEnabled.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(32.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.work_reminder_toggle_label)
            )
            Switch(
                checked = isReminderEnabled,
                onCheckedChange = component::onToggleReminder
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkManagerPreview() {
    AppTheme {
        WorkManagerUi(FakeWorkManagerComponent())
    }
}
