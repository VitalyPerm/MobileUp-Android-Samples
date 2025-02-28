package ru.mobileup.samples.features.calendar.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.features.calendar.domain.CalendarEvent
import ru.mobileup.samples.features.calendar.domain.CalendarPeriod
import java.time.LocalDate

class FakeCalendarComponent : CalendarComponent {
    override val selectedCalendarPeriod = MutableStateFlow(CalendarPeriod.Year)
    override val selectedCalendarDay = MutableStateFlow(null)
    override val calendarEvents = MutableStateFlow(CalendarEvent.MOCK_LIST)

    override fun changeCalendarPeriod(period: CalendarPeriod) = Unit
    override fun onCalendarDateClick(date: LocalDate) = Unit
}
