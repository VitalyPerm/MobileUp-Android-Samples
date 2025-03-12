package ru.mobileup.samples.features.image.presentation.viewer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

class FakeImageViewerComponent : ImageViewerComponent {
    override val imageCarousel: StateFlow<ImageCarousel> = MutableStateFlow(ImageCarousel.MOCK)

    override fun onPageChanged(page: ImageCarouselPage) = Unit
}