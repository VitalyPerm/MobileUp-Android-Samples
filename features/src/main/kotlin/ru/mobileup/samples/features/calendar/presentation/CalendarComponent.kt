package ru.mobileup.samples.features.calendar.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.calendar.domain.CalendarDay
import ru.mobileup.samples.features.calendar.domain.CalendarEvent
import ru.mobileup.samples.features.calendar.domain.CalendarPeriod
import java.time.LocalDate

interface CalendarComponent {
    val selectedCalendarPeriod: StateFlow<CalendarPeriod>
    val selectedCalendarDay: StateFlow<CalendarDay?>
    val calendarEvents: StateFlow<List<CalendarEvent>>

    fun changeCalendarPeriod(period: CalendarPeriod)
    fun onCalendarDateClick(date: LocalDate)
}
