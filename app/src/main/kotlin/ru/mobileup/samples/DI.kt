package ru.mobileup.samples

import ru.mobileup.samples.core.coreModule

val allModules = listOf(
    coreModule(BuildConfig.BACKEND_URL)
)