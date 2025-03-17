package ru.mobileup.samples.features.image.presentation

import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.presentation.carousel.ImageCarouselComponent

interface ImageComponent {

    val imageCarouselComponent: ImageCarouselComponent

    val title: StateFlow<StringDesc>
    val description: StateFlow<StringDesc>
}