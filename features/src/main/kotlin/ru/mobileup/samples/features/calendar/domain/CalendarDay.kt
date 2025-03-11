package ru.mobileup.samples.features.calendar.domain

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate?,
    val events: List<CalendarEvent>
)
