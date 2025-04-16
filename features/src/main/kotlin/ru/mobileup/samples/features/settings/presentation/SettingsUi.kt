package ru.mobileup.samples.features.settings.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.app_settings.appLanguageAsState
import ru.mobileup.samples.core.app_settings.domain.AppLanguage
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.app_settings.domain.AppTheme
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.settings.presentation.widget.SettingsPicker

@Composable
fun SettingsUi(
    component: SettingsComponent,
    modifier: Modifier = Modifier,
) {
    val settings by component.settings.collectAsState()
    val appLanguage by appLanguageAsState()

    LaunchedEffect(appLanguage) {
        component.onLanguageChange()
    }

    SettingsContent(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(vertical = 32.dp),
        settings = settings,
        onThemeClick = component::onThemeClick,
        onLanguageClick = component::onLanguageClick
    )
}

@Composable
private fun SettingsContent(
    settings: AppSettings,
    onThemeClick: (AppTheme) -> Unit,
    onLanguageClick: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ThemePicker(
            theme = settings.theme,
            onThemeSelect = onThemeClick,
        )
        LanguagePicker(
            language = settings.language,
            onLanguageSelect = onLanguageClick
        )
    }
}

@Composable
private fun ThemePicker(
    theme: AppTheme,
    onThemeSelect: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    SettingsPicker(
        modifier = modifier,
        title = stringResource(R.string.settings_theme),
        selectedOption = theme,
        options = AppTheme.entries,
        onOptionSelect = onThemeSelect,
        displayOptionMapper = { context.resources.getString(it.displayNameRes) }
    )
}

private val AppTheme.displayNameRes: Int
    @StringRes
    get() = when (this) {
        AppTheme.Light -> R.string.settings_theme_light
        AppTheme.Dark -> R.string.settings_theme_dark
        AppTheme.System -> R.string.settings_theme_system
    }

@Composable
private fun LanguagePicker(
    language: AppLanguage,
    onLanguageSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    SettingsPicker(
        modifier = modifier,
        title = stringResource(R.string.settings_language),
        selectedOption = language,
        options = AppLanguage.entries,
        onOptionSelect = onLanguageSelect,
        displayOptionMapper = { context.resources.getString(it.displayNameRes) }
    )
}

private val AppLanguage.displayNameRes: Int
    @StringRes
    get() = when (this) {
        AppLanguage.EN -> R.string.settings_language_en
        AppLanguage.RU -> R.string.settings_language_ru
    }

@Preview
@Composable
private fun SettingsUiPreview() {
    AppTheme {
        SettingsUi(FakeSettingsComponent())
    }
}
