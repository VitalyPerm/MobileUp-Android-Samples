package ru.mobileup.samples.features.work_manager

import androidx.work.WorkManager
import com.arkivanov.decompose.ComponentContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.work_manager.data.ReminderScheduler
import ru.mobileup.samples.features.work_manager.data.ReminderSchedulerImpl
import ru.mobileup.samples.features.work_manager.data.workers.FrequentNotificationWorker
import ru.mobileup.samples.features.work_manager.presentation.RealWorkManagerComponent
import ru.mobileup.samples.features.work_manager.presentation.WorkManagerComponent

val workManagerModule = module {
    single { WorkManager.getInstance(get()) }
    single<ReminderScheduler> { ReminderSchedulerImpl(get()) }
    workerOf(::FrequentNotificationWorker)
}

fun ComponentFactory.createWorkManagerComponent(
    componentContext: ComponentContext
): WorkManagerComponent = RealWorkManagerComponent(componentContext, get(), get())
