package ru.mobileup.samples.features.image.presentation.carousel

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

interface ImageCarouselComponent {
    val imageCarousel: StateFlow<ImageCarousel>

    fun onCarouselPageChanged(imageCarouselPage: ImageCarouselPage)
    fun onImageClick()

    sealed interface Output {
        data class ViewerRequested(val imageCarousel: ImageCarousel) : Output
    }
}