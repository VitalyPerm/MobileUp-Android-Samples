package ru.mobileup.samples.features.divkit.data

import divkit.dsl.Action
import divkit.dsl.Background
import divkit.dsl.Color
import divkit.dsl.Url
import divkit.dsl.animation
import divkit.dsl.bold
import divkit.dsl.border
import divkit.dsl.center
import divkit.dsl.color
import divkit.dsl.container
import divkit.dsl.core.reference
import divkit.dsl.defer
import divkit.dsl.edgeInsets
import divkit.dsl.end
import divkit.dsl.horizontal
import divkit.dsl.image
import divkit.dsl.matchParentSize
import divkit.dsl.stroke
import divkit.dsl.template
import divkit.dsl.text

object DivKitTemplate {

    object CategoryTitle {
        val titleRef = reference<String>("productTitle")
        val clickActionRef = reference<Action>("onProductTitleClick")
        val backgroundColorRef = reference<List<Background>>("productTitleBgColor")
        val textColorRef = reference<Color>("productTitleColor")
        val template by lazy(LazyThreadSafetyMode.NONE) {
            template("productCategory") {
                text(
                    border = border(
                        cornerRadius = 16,
                        stroke = stroke(
                            color = color("#000000")
                        )
                    ),
                    paddings = edgeInsets(8),
                    margins = edgeInsets(4),
                    textAlignmentHorizontal = center,
                )
                    .defer(
                        text = titleRef,
                        action = clickActionRef,
                        background = backgroundColorRef,
                        textColor = textColorRef
                    )
            }
        }
    }

    object Product {
        val titleRef = reference<String>("productName")
        val weightRef = reference<String>("productWeight")
        val clickActionRef = reference<Action>("onProductClick")
        val iconUrl = reference<Url>("icon")

        val template by lazy(LazyThreadSafetyMode.NONE) {
            template("product") {
                container(
                    orientation = horizontal,
                    width = matchParentSize(),
                    paddings = edgeInsets(8),
                    margins = edgeInsets(8),
                    border = border(
                        cornerRadius = 4,
                        stroke = stroke(
                            color = color("#000000")
                        )
                    ),
                    contentAlignmentVertical = center,
                    items = listOf(
                        text(
                            fontSize = 20
                        )
                            .defer(text = titleRef),
                        image(
                            border = border(
                                cornerRadius = 24,
                                stroke = stroke(
                                    color = color("#000000")
                                )
                            ),
                        ).defer(imageUrl = iconUrl),
                        text(
                            textAlignmentHorizontal = end,
                            fontWeight = bold
                        )
                            .defer(text = weightRef),
                    ),
                    actionAnimation = animation(
                        duration = 500
                    )
                )
                    .defer(action = clickActionRef)
            }
        }
    }
}
