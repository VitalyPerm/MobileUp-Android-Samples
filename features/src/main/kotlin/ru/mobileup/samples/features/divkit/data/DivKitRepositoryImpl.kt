package ru.mobileup.samples.features.divkit.data

import android.content.Context
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div2.DivData
import kotlinx.coroutines.delay
import ru.mobileup.samples.features.divkit.domain.DivKitUiData

class DivKitRepositoryImpl(
    private val context: Context,
    private val parser: DivParsingEnvironment
) : DivKitRepository {

    override suspend fun getMenuData(): DivData {
        val uiData = getUiData("menu")
        delay(1000)
        return DivData(
            env = parser.apply { uiData.templates?.let(::parseTemplates) },
            json = uiData.card
        )
    }

    override suspend fun getView(jsonName: String): DivData {
        val uiData = getUiData(jsonName)
        delay(1000)
        return DivData(
            env = parser.apply { uiData.templates?.let(::parseTemplates) },
            json = uiData.card
        )
    }

    private fun getUiData(jsonName: String) = context.assets.open("divkit/$jsonName.json")
        .bufferedReader()
        .use { it.readText() }
        .let(DivKitUiData::fromString)
}