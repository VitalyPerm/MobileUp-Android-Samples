package ru.mobileup.samples.features.settings.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.app_settings.domain.AppTheme

class FakeSettingsComponent : SettingsComponent {

    override val settings = MutableStateFlow(AppSettings())

    override fun onThemeClick(theme: AppTheme) = Unit

    override fun onLanguageClick(language: AppLanguage) = Unit

    override fun onLanguageChange() = Unit
}
