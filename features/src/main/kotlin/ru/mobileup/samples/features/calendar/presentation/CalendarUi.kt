package ru.mobileup.samples.features.calendar.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.features.calendar.domain.CalendarPeriod
import ru.mobileup.samples.features.calendar.presentation.wigets.MonthCalendarContent
import ru.mobileup.samples.features.calendar.presentation.wigets.WeekCalendarContent
import ru.mobileup.samples.features.calendar.presentation.wigets.YearCalendarContent

@Composable
fun CalendarUi(
    component: CalendarComponent,
    modifier: Modifier = Modifier
) {
    val selectedCalendarPeriod by component.selectedCalendarPeriod.collectAsState()
    val selectedCalendarDay by component.selectedCalendarDay.collectAsState()
    val calendarEvents by component.calendarEvents.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        when (selectedCalendarPeriod) {
            CalendarPeriod.Week -> WeekCalendarContent(
                calendarEvents = calendarEvents,
                selectedCalendarDay = selectedCalendarDay,
                onClick = component::onCalendarDateClick
            )

            CalendarPeriod.Month -> MonthCalendarContent()
            CalendarPeriod.Year -> YearCalendarContent()
        }

        CalendarPeriods(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            selectedCalendarPeriod = selectedCalendarPeriod,
            onClick = component::changeCalendarPeriod
        )
    }
}

@Composable
private fun BoxScope.CalendarPeriods(
    selectedCalendarPeriod: CalendarPeriod,
    onClick: (CalendarPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarPeriod.entries.forEach { period ->
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedCalendarPeriod == period,
                    onClick = { onClick(period) }
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = period.name,
                    color = CustomTheme.colors.text.primary,
                    style = CustomTheme.typography.button.bold,
                )
            }
        }
    }
}

@Preview
@Composable
private fun MenuUiPreview() {
    AppTheme {
        CalendarUi(FakeCalendarComponent())
    }
}
