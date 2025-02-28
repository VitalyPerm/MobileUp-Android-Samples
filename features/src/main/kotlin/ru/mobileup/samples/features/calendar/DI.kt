package ru.mobileup.samples.features.calendar

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.calendar.presentation.CalendarComponent
import ru.mobileup.samples.features.calendar.presentation.RealCalendarComponent

fun ComponentFactory.createCalendarComponent(componentContext: ComponentContext): CalendarComponent {
    return RealCalendarComponent(componentContext)
}
