package ru.mobileup.samples.features.image.presentation.carousel

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.domain.ImageCarousel

interface ImageCarouselComponent {
    val imageCarousel: StateFlow<ImageCarousel>

    val mode: StateFlow<ImageCarouselMode>

    fun onCarouselPageChanged(position: Int)
    fun onImageClick()
}