package ru.mobileup.samples.features.divkit.data.api

import com.fasterxml.jackson.databind.json.JsonMapper
import divkit.dsl.State
import divkit.dsl.action
import divkit.dsl.bold
import divkit.dsl.center
import divkit.dsl.color
import divkit.dsl.container
import divkit.dsl.core.bind
import divkit.dsl.data
import divkit.dsl.divan
import divkit.dsl.edgeInsets
import divkit.dsl.end
import divkit.dsl.fixedSize
import divkit.dsl.horizontal
import divkit.dsl.image
import divkit.dsl.matchParentSize
import divkit.dsl.overlap
import divkit.dsl.render
import divkit.dsl.scope.DivScope
import divkit.dsl.solidBackground
import divkit.dsl.state
import divkit.dsl.stateItem
import divkit.dsl.text
import divkit.dsl.url
import divkit.dsl.vertical
import kotlinx.coroutines.delay
import ru.mobileup.samples.features.divkit.data.DivKitTemplate
import ru.mobileup.samples.features.divkit.data.db.DivKitGoodsDb
import ru.mobileup.samples.features.divkit.data.db.DivKitProductDBO
import ru.mobileup.samples.features.divkit.domain.DivKitProduct
import ru.mobileup.samples.features.divkit.domain.DivKitProductType
import ru.mobileup.samples.features.divkit.presentation.goods.list.DivKitGoodsListComponent

class DivKitApiImpl(
    private val db: DivKitGoodsDb,
    private val jsonMapper: JsonMapper
) : DivKitApi {

    override suspend fun getGoodsListData(
        sortByNameEnable: Boolean,
        sortByWeightEnable: Boolean
    ): String {
        delay(1000)
        val goods = db.getGoods(
            sortByNameEnable = sortByNameEnable,
            sortByWeightEnable = sortByWeightEnable
        ).map(DivKitProductDBO::toDomain)
        val categoriesList = listOf(
            "All",
            DivKitProductType.Fruit.name,
            DivKitProductType.Vegetable.name
        )
        val filters = listOf(
            DivKitGoodsListComponent.Sort.BY_NAME to sortByNameEnable,
            DivKitGoodsListComponent.Sort.BY_WEIGHT to sortByWeightEnable
        )

        val card = divan {
            data(
                logId = "goods_list_data",
                div = state(
                    id = "goods_list_state",
                    states = categoriesList.map { category ->
                        item(
                            currentCategory = category,
                            categories = categoriesList,
                            goods = goods,
                            filters = filters
                        )
                    }
                )
            )
        }

        return jsonMapper.writeValueAsString(card)
    }

    override suspend fun getProductById(id: Long): String {
        val product = db.getProductById(id)

        val card = divan {
            data(
                logId = "product_details_$id",
                div = container(
                    items = listOf(
                        container(
                            orientation = overlap,
                            contentAlignmentVertical = center,
                            items = listOf(
                                image(
                                    imageUrl = url("divkit-asset://arrow_back.webp"),
                                    width = fixedSize(32),
                                    height = fixedSize(32),
                                    margins = edgeInsets(16),
                                    action = action(
                                        logId = "product_details_back_button_click",
                                        url = url("div-action://back")
                                    )
                                ),
                                text(
                                    text = product.name,
                                    textAlignmentHorizontal = center,
                                    fontSize = 36,
                                    fontWeight = bold,
                                    margins = edgeInsets(16)
                                )
                            )
                        ),
                        container(
                            orientation = horizontal,
                            margins = edgeInsets(8),
                            paddings = edgeInsets(8),
                            items = listOf(
                                text(
                                    text = "Balance",
                                    fontSize = 24
                                ),
                                text(
                                    text = "${product.weight}g",
                                    fontSize = 24,
                                    textAlignmentHorizontal = end
                                )
                            )
                        ),
                        image(
                            imageUrl = url(product.imageUrl),
                            width = matchParentSize()
                        )
                    )
                )
            )
        }

        return jsonMapper.writeValueAsString(card)
    }
}

private fun DivScope.item(
    currentCategory: String,
    categories: List<String>,
    goods: List<DivKitProduct>,
    filters: List<Pair<DivKitGoodsListComponent.Sort, Boolean>>
): State.Item {
    val filteredGoods = when (currentCategory) {
        DivKitProductType.Fruit.name -> goods.filter { it.type == DivKitProductType.Fruit }
        DivKitProductType.Vegetable.name -> goods.filter { it.type == DivKitProductType.Vegetable }
        else -> goods
    }
    return stateItem(
        stateId = currentCategory,
        div = container(
            items = listOf(
                container(
                    orientation = horizontal,
                    margins = edgeInsets(16),
                    items = filters.map { (filter, enabled) ->
                        with(DivKitTemplate.CategoryTitle) {
                            render(
                                template,
                                titleRef bind filter.title,
                                backgroundColorRef bind listOf(
                                    solidBackground(
                                        color = color(if (enabled) "#3F54DF" else "#FFFFFFFF")
                                    )
                                ),
                                textColorRef bind color(if (enabled) "#FFFFFFFF" else "#000000"),
                                clickActionRef bind action(
                                    logId = "on_filter_category_click_$filter",
                                    url = url(DivKitGoodsListComponent.createApplySortUrl(filter.title))
                                ),
                            )
                        }
                    }
                ),
                container(
                    orientation = horizontal,
                    margins = edgeInsets(16),
                    items = categories.map { category ->
                        with(DivKitTemplate.CategoryTitle) {
                            render(
                                template,
                                titleRef bind category,
                                backgroundColorRef bind listOf(
                                    solidBackground(
                                        color = color(if (currentCategory == category) "#3F54DF" else "#FFFFFFFF")
                                    )
                                ),
                                textColorRef bind color(if (currentCategory == category) "#FFFFFFFF" else "#000000"),
                                clickActionRef bind action(
                                    logId = "on_product_category_click_$category",
                                    url = url("div-action://set_state?state_id=0/goods_list_state/$category")
                                ),
                            )
                        }
                    }
                ),
                container(
                    orientation = vertical,
                    items = filteredGoods.map { product ->
                        with(DivKitTemplate.Product) {
                            render(
                                template,
                                titleRef bind product.name,
                                weightRef bind "${product.weight}g",
                                clickActionRef bind action(
                                    logId = "on_product_click_${product.id}",
                                    url = url(DivKitGoodsListComponent.createOpenDetailsUrl(product.id))
                                ),
                                iconUrl bind url(product.imageUrl)
                            )
                        }
                    }
                )
            )
        )
    )
}

private val productsList = listOf(
    DivKitProductDBO(
        id = 0,
        name = "Banana",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://banana.webp"
    ),
    DivKitProductDBO(
        id = 1,
        name = "Potato",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://potato.webp"
    ),
    DivKitProductDBO(
        id = 2,
        name = "Carrot",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://carrot.webp"
    ),
    DivKitProductDBO(
        id = 3,
        name = "Apple",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://apple.webp"
    ),
    DivKitProductDBO(
        id = 4,
        name = "Cherry",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://cherry.webp"
    ),
    DivKitProductDBO(
        id = 5,
        name = "Grape",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://grape.webp"
    ),
    DivKitProductDBO(
        id = 6,
        name = "Mango",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://mango.webp"
    ),
    DivKitProductDBO(
        id = 7,
        name = "Watermelon",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://watermelon.webp"
    ),
    DivKitProductDBO(
        id = 8,
        name = "Pear",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://pear.webp"
    ),
    DivKitProductDBO(
        id = 9,
        name = "Lime",
        type = DivKitProductType.Fruit.name,
        imageUrl = "divkit-asset://lime.webp"
    ),
    DivKitProductDBO(
        id = 10,
        name = "Peas",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://peas.webp"
    ),
    DivKitProductDBO(
        id = 11,
        name = "Beet",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://beet.webp"
    ),
    DivKitProductDBO(
        id = 12,
        name = "Pepper",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://pepper.webp"
    ),
    DivKitProductDBO(
        id = 13,
        name = "Radish",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://radish.webp"
    ),
    DivKitProductDBO(
        id = 14,
        name = "Cucumber",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://cucumber.webp"
    ),
    DivKitProductDBO(
        id = 15,
        name = "Bean",
        type = DivKitProductType.Vegetable.name,
        imageUrl = "divkit-asset://bean.webp"
    )
)