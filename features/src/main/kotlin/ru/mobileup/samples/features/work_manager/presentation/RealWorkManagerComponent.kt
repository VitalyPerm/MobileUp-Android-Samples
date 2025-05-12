package ru.mobileup.samples.features.work_manager.presentation

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.permissions.PermissionService
import ru.mobileup.samples.core.permissions.SinglePermissionResult
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.work_manager.data.ReminderScheduler

class RealWorkManagerComponent(
    componentContext: ComponentContext,
    private val reminderScheduler: ReminderScheduler,
    private val permissionService: PermissionService,
) : ComponentContext by componentContext, WorkManagerComponent {

    private val isPermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionService.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

    private val isNotificationPermissionGranted = MutableStateFlow(isPermissionGranted)

    override val isReminderEnabled = combine(
        isNotificationPermissionGranted,
        reminderScheduler.isReminderActive
    ) { isGranted, isActive ->
        isGranted && isActive
    }.stateIn(componentScope, SharingStarted.Eagerly, false)

    init {
        doOnResume {
            isNotificationPermissionGranted.value = isPermissionGranted
        }
    }

    override fun onToggleReminder(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionIfNeeded { isGranted ->
                if (isGranted) updateReminder(enable)
            }
        } else {
            updateReminder(enable)
        }
    }

    private fun updateReminder(enable: Boolean) = reminderScheduler.run {
        if (enable) scheduleFrequentReminder() else cancelReminder()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermissionIfNeeded(onResult: (Boolean) -> Unit) {
        if (isPermissionGranted) {
            onResult(true)
        } else {
            componentScope.launch {
                val result =
                    permissionService.requestPermission(Manifest.permission.POST_NOTIFICATIONS)

                onResult(result is SinglePermissionResult.Granted)
            }
        }
    }
}
