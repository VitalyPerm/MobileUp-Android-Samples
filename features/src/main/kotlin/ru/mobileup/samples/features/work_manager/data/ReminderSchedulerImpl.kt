package ru.mobileup.samples.features.work_manager.data

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.map
import ru.mobileup.samples.features.work_manager.data.workers.FrequentNotificationWorker
import java.util.concurrent.TimeUnit

class ReminderSchedulerImpl(
    private val workManager: WorkManager,
) : ReminderScheduler {

    companion object {
        private const val WORK_NAME = "reminder_notification"
        private const val REPEAT_INTERVAL = 15L
    }

    override val isReminderActive = workManager.getWorkInfosForUniqueWorkFlow(WORK_NAME).map {
        it.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
    }

    override fun scheduleFrequentReminder() {
        val request = PeriodicWorkRequestBuilder<FrequentNotificationWorker>(
            REPEAT_INTERVAL,
            TimeUnit.MINUTES
        )
            .setInitialDelay(REPEAT_INTERVAL, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun cancelReminder() {
        workManager.cancelUniqueWork(WORK_NAME)
    }
}
