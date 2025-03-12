package ru.mobileup.samples.features.image.presentation.viewer

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

interface ImageViewerComponent {
    val imageCarousel: StateFlow<ImageCarousel>

    fun onPageChanged(page: ImageCarouselPage)
}