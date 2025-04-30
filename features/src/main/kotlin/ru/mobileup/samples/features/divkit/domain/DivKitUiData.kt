package ru.mobileup.samples.features.divkit.domain

import org.json.JSONObject
import ru.mobileup.samples.core.error_handling.DeserializationException

data class DivKitUiData(
    val card: JSONObject,
    val templates: JSONObject?
) {
    companion object {

        private const val CARD_OBJECT = "card"
        private const val TEMPLATES_OBJECT = "templates"

        fun fromString(json: String): DivKitUiData = JSONObject(json).run {
            DivKitUiData(
                card = getJSONObject(CARD_OBJECT) ?: throw DeserializationException(
                    IllegalArgumentException("DivKitUiData.fromJson error while parse $json")
                ),
                templates = getJSONObject(TEMPLATES_OBJECT)
            )
        }
    }
}
