package ru.mobileup.samples.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.theme.custom.toMaterialColors
import ru.mobileup.samples.core.theme.custom.toMaterialTypography
import ru.mobileup.samples.core.theme.values.AppTypography
import ru.mobileup.samples.core.theme.values.DarkAppColors
import ru.mobileup.samples.core.theme.values.LightAppColors

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkAppColors else LightAppColors
    val typography = AppTypography

    CustomTheme(
        colors = colorScheme,
        typography = typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme.toMaterialColors(),
            typography = typography.toMaterialTypography(),
            content = content
        )
    }
}