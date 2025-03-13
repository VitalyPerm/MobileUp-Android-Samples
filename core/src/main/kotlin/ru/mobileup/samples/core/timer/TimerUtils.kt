package ru.mobileup.samples.core.timer

import ru.mobileup.samples.core.utils.formatSecondsToMS

fun TimerState.isTicking() = this is TimerState.CountingDown

fun TimerState.timerFormat(): String {
    return when (this) {
        TimerState.Idle -> ""
        is TimerState.CountingDown -> timerFormat()
    }
}

fun TimerState.CountingDown.timerFormat() = formatSecondsToMS(this.time)