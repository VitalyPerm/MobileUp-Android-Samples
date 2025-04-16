package ru.mobileup.samples.core.language

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ru.mobileup.samples.core.activity.ActivityProvider
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import java.util.Locale

class LanguageServiceImpl(
    private val activityProvider: ActivityProvider,
) : LanguageService {

    private val currentLocale: Locale
        get() = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()

    override fun getLanguage(): AppLanguage = AppLanguage.fromLocale(currentLocale)

    override fun setLanguage(language: AppLanguage) {
        if (language == getLanguage()) return

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))

        /**
         * On Android < 13, AppCompatDelegate.setApplicationLocales doesn't trigger LocalConfiguration changes.
         * We need to recreate the activity to apply the new locale.
         * On Android 13+, the per-app language API handles this automatically.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            activityProvider.activity?.recreate()
        }
    }
}
