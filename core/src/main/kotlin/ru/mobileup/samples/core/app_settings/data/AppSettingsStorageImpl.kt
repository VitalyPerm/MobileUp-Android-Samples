package ru.mobileup.samples.core.app_settings.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.app_settings.domain.AppTheme
import ru.mobileup.samples.core.language.LanguageService
import ru.mobileup.samples.core.settings.SettingsFactory

class AppSettingsStorageImpl(
    settingsFactory: SettingsFactory,
    private val languageService: LanguageService,
) : AppSettingsStorage {

    companion object {
        private const val SETTINGS_NAME = "app_settings"
        private const val KEY_THEME = "app_settings_theme"
    }

    private val settingsPrefs = settingsFactory.createSettings(SETTINGS_NAME)

    override val settings = MutableStateFlow(
        runBlocking {
            AppSettings(
                theme = getTheme(),
                language = getLanguage(),
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

    override fun getLanguage(): AppLanguage = languageService.getLanguage()

    override fun setLanguage(language: AppLanguage) = languageService.setLanguage(language).also {
        settings.update { it.copy(language = language) }
    }

    /**
     * Syncs the app's language state with the current system locale or per-app locale (Android 13+).
     *
     * This is typically called after the language has been changed externally —
     * either via system settings or per-app language preferences.
     */
    override fun syncLanguage() = settings.update { it.copy(language = getLanguage()) }
}
