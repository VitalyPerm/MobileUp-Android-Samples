package ru.mobileup.samples.core.app_settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import java.util.Locale

@Composable
fun appLanguageAsState(): State<AppLanguage> {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        derivedStateOf {
            AppLanguage.fromLocale(
                AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
            )
        }
    }
}
