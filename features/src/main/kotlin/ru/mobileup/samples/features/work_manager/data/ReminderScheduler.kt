package ru.mobileup.samples.features.work_manager.data

import kotlinx.coroutines.flow.Flow

interface ReminderScheduler {

    val isReminderActive: Flow<Boolean>

    fun scheduleFrequentReminder()

    fun cancelReminder()
}
