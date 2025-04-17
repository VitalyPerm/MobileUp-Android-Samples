package ru.mobileup.samples.core.app_settings.domain

import java.util.Locale

enum class AppLanguage {
    EN, RU;

    val tag: String
        get() = when (this) {
            EN -> EN_TAG
            RU -> RU_TAG
        }

    companion object {
        val DEFAULT = EN

        fun fromLocale(locale: Locale): AppLanguage = when (locale.language) {
            EN_TAG -> EN
            RU_TAG -> RU
            else -> DEFAULT
        }

        private const val EN_TAG = "en"
        private const val RU_TAG = "ru"
    }
}
