package ru.mobileup.samples.core.theme.component

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.app_settings.domain.AppTheme

class FakeThemeComponent : ThemeComponent {

    override val theme = MutableStateFlow(AppTheme.System)
}
