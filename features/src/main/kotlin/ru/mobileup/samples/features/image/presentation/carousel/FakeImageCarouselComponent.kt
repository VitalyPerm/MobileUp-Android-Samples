package ru.mobileup.samples.features.image.presentation.carousel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

class FakeImageCarouselComponent : ImageCarouselComponent {

    override val imageCarousel: StateFlow<ImageCarousel> = MutableStateFlow(ImageCarousel.MOCK)

    override fun onCarouselPageChanged(imageCarouselPage: ImageCarouselPage) = Unit
    override fun onImageClick() = Unit
}