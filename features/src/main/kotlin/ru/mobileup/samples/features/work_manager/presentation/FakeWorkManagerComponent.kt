package ru.mobileup.samples.features.work_manager.presentation

import kotlinx.coroutines.flow.MutableStateFlow

class FakeWorkManagerComponent : WorkManagerComponent {

    override val isReminderEnabled = MutableStateFlow(true)

    override fun onToggleReminder(enable: Boolean) = Unit
}
