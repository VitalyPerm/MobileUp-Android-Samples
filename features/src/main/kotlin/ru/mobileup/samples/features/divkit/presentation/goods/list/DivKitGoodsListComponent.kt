package ru.mobileup.samples.features.divkit.presentation.goods.list

import com.yandex.div.core.view2.Div2View
import kotlinx.coroutines.flow.StateFlow

interface DivKitGoodsListComponent {

    val content: StateFlow<Div2View?>

    sealed interface Output {
        data class DetailsRequested(val id: Long) : Output
    }

    enum class Sort(val title: String) {
        BY_NAME("Sort by name"), BY_WEIGHT("Sort by weight"), Unknown("Unknown");

        companion object {
            fun fromString(string: String?) = when (string) {
                BY_NAME.title -> BY_NAME
                BY_WEIGHT.title -> BY_WEIGHT
                else -> Unknown
            }
        }
    }

    companion object {
        const val DETAILS_URL_PATH = "div_action://details"
        const val OPEN_DETAILS = "open_details"
        const val QUERY_ID = "id"
        const val APPLY_SORT = "apply_sort"
        const val SORT_NAME = "sort_name"
        fun createOpenDetailsUrl(id: Long): String = "$DETAILS_URL_PATH/$OPEN_DETAILS?$QUERY_ID=$id"
        fun createApplySortUrl(
            sortName: String
        ): String = "$DETAILS_URL_PATH/$APPLY_SORT?$SORT_NAME=$sortName"
    }
}