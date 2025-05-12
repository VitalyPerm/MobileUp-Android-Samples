package ru.mobileup.samples.features.work_manager.presentation

import kotlinx.coroutines.flow.StateFlow

interface WorkManagerComponent {

    val isReminderEnabled: StateFlow<Boolean>

    fun onToggleReminder(enable: Boolean)
}
