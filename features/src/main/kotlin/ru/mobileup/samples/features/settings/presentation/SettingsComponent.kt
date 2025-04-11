package ru.mobileup.samples.features.settings.presentation

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.app_settings.domain.AppTheme

interface SettingsComponent {

    val settings: StateFlow<AppSettings>

    fun onThemeClick(theme: AppTheme)
}
