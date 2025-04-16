package ru.mobileup.samples.features.calendar.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.kizitonwose.calendar.core.atStartOfMonth
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

val YearMonth.localizedMonthName: String
    @Composable
    get() {
        val config = LocalConfiguration.current
        val locale = config.locales[0] ?: Locale.getDefault()
        val formatter = DateTimeFormatter.ofPattern("LLLL", locale)
        return atStartOfMonth().format(formatter)
    }
