package ru.mobileup.samples.core.app_settings.domain

data class AppSettings(
    val theme: AppTheme = AppTheme.System,
    val language: AppLanguage = AppLanguage.DEFAULT,
)
