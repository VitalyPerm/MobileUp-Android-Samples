package ru.mobileup.samples.core.language

import ru.mobileup.samples.core.app_settings.domain.AppLanguage

interface LanguageService {

    fun getLanguage(): AppLanguage

    fun setLanguage(language: AppLanguage)
}
