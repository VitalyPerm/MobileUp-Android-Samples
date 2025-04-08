package ru.mobileup.samples.core.theme.component

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.core.app_settings.data.AppSettingsStorage
import ru.mobileup.samples.core.app_settings.domain.AppSettings
import ru.mobileup.samples.core.utils.computed

class RealThemeComponent(
    componentContext: ComponentContext,
    appSettingsStorage: AppSettingsStorage,
) : ComponentContext by componentContext, ThemeComponent {

    override val theme = computed(appSettingsStorage.settings, AppSettings::theme)
}
