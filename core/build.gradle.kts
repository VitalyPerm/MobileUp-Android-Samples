import java.util.Properties
import kotlin.apply

// Function to read properties from local.properties
fun getLocalProperty(key: String, project: Project): String? {
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val properties = Properties().apply { load(localPropertiesFile.inputStream()) }
        return properties.getProperty(key)
    }
    return null
}

plugins {
    alias(libs.plugins.convetion.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}

android {
    buildFeatures {
        buildConfig = true
    }

    namespace = "ru.mobileup.samples.core"

    defaultConfig {
        val googleMapsApiKey = getLocalProperty("google.map.api.key", project) ?: "not_found"
        manifestPlaceholders["GOOGLE_MAP_API_KEY"] = googleMapsApiKey
        buildConfigField("String", "GOOGLE_MAP_API_KEY", "\"$googleMapsApiKey\"")

        val yandexMapApiKey = getLocalProperty("yandex.map.api.key", project) ?: "\"not_found\""
        buildConfigField("String", "YANDEX_MAP_API_KEY", yandexMapApiKey)
    }
}

dependencies {
    ksp(libs.ktorfit.ksp)

    // Kotlin
    implementation(libs.kotlinx.datetime)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // UI
    implementation(libs.bundles.compose)
    implementation(libs.compose.material.icons)
    // TODO( Remove when a stable release is available)
    implementation("androidx.compose.material3:material3:1.4.0-alpha10")
    implementation(libs.bundles.accompanist)

    // DI
    implementation(libs.koin)

    // Logging
    implementation(libs.logger.kermit)

    // Network
    implementation(libs.bundles.ktor)
    implementation(libs.ktorfit.lib)

    // Security
    implementation(libs.security.crypto)
    implementation(libs.security.crypto.ktx)
    implementation(libs.biometrics)

    // Architecture
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.replica)
    api(libs.moko.resources)
    implementation(libs.moko.resourcesCompose)

    implementation(libs.form.validation)

    // location
    implementation(libs.gms.location)
    implementation(libs.gms.coroutines)

    // Geo
    implementation(libs.yandex.mapkit)
    implementation(libs.bundles.google.maps)

    // Debugging
    debugImplementation(libs.chucker)
    debugImplementation(libs.bundles.hyperion)
    debugImplementation(libs.replica.devtools)
}
