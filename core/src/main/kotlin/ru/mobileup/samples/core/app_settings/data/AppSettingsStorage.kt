package ru.mobileup.samples.core.app_settings.data

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.app_settings.domain.AppTheme

interface AppSettingsStorage {

    val settings: StateFlow<AppSettings>

    suspend fun getTheme(): AppTheme
    suspend fun setTheme(theme: AppTheme)

    fun getLanguage(): AppLanguage
    fun setLanguage(language: AppLanguage)
    fun syncLanguage()
}
