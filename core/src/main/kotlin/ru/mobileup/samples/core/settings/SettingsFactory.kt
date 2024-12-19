package ru.mobileup.samples.core.settings

interface SettingsFactory {

    fun createSettings(name: String): Settings

    fun createEncryptedSettings(name: String): Settings
}
