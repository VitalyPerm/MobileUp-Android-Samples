package ru.mobileup.samples.core.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

sealed interface TimerState {
    data class Tick(val time: Long) : TimerState
    data object Finish : TimerState
}

class Timer(private val coroutineScope: CoroutineScope) {

    private var timerJob: Job? = null

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Finish)
    val timerState = _timerState.asStateFlow()

    fun start(timerTimeInSeconds: Long) {
        cancel()
        timerJob = coroutineScope.launch {
            val timeInTheFuture = Clock.System.now().epochSeconds + timerTimeInSeconds
            var tick = timerTimeInSeconds
            while (tick != 0L) {
                tick = (timeInTheFuture - Clock.System.now().epochSeconds).coerceAtLeast(0)
                _timerState.value = TimerState.Tick(tick)
                delay(1.seconds)
            }
        }.apply {
            invokeOnCompletion {
                _timerState.value = TimerState.Finish
            }
        }
    }

    fun cancel() {
        timerJob?.cancel()
        timerJob = null
    }
}