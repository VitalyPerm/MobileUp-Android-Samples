package ru.mobileup.samples.core.timer

import ru.mobileup.samples.core.utils.formatSecondsToMs

fun TimerState.isTicking() = this is TimerState.Tick

fun TimerState.timerFormat(): String {
    return when (this) {
        TimerState.Finish -> ""
        is TimerState.Tick -> timerFormat()
    }
}

fun TimerState.Tick.timerFormat() = formatSecondsToMs(this.time)