package ru.mobileup.samples.features.chat.presentation.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.features.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DATE_FORMAT = "d MMMM"

@Composable
fun DateDivider(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val text = when {
        date.isEqual(today) -> stringResource(R.string.chat_divider_today)
        date.isEqual(yesterday) -> stringResource(R.string.chat_divider_yesterday)
        else -> date.format(DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.getDefault()))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = CustomTheme.colors.chat.input,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                style = CustomTheme.typography.caption.regular,
                color = CustomTheme.colors.text.secondary
            )
        }
    }
}

@Preview
@Composable
private fun DateDividerPreview() {
    AppTheme {
        Column {
            DateDivider(LocalDate.now().minusDays(10))
            DateDivider(LocalDate.now().minusDays(1))
            DateDivider(LocalDate.now())
        }
    }
}