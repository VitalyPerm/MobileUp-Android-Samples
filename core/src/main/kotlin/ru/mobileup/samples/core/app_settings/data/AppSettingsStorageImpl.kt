package ru.mobileup.samples.core.app_settings.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.settings.SettingsFactory
import ru.mobileup.samples.core.theme.AppTheme

class AppSettingsStorageImpl(
    settingsFactory: SettingsFactory,
) : AppSettingsStorage {

    companion object {
        private const val SETTINGS_NAME = "user_settings"
        private const val KEY_THEME = "user_settings_theme"
    }

    private val settingsPrefs = settingsFactory.createSettings(SETTINGS_NAME)

    override val settings = MutableStateFlow(
        runBlocking {
            AppSettings(
                theme = getTheme()
            )
        }
    )

    override suspend fun getTheme(): AppTheme = settingsPrefs.getString(KEY_THEME)?.let {
        runCatching { AppTheme.valueOf(it) }.getOrElse { AppTheme.System }
    } ?: AppTheme.System

    override suspend fun setTheme(theme: AppTheme) = settingsPrefs.putString(
        KEY_THEME, theme.name
    ).also {
        settings.update { it.copy(theme = theme) }
    }
}
