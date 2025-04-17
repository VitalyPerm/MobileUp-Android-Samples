package ru.mobileup.samples.core.theme.component

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.app_settings.domain.AppTheme

interface ThemeComponent {

    val theme: StateFlow<AppTheme>
}
