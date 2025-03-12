package ru.mobileup.samples.features.image.presentation.viewer

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

class RealImageViewerComponent(
    componentContext: ComponentContext,
    imageCarousel: ImageCarousel
) : ComponentContext by componentContext, ImageViewerComponent {

    override val imageCarousel: MutableStateFlow<ImageCarousel> = MutableStateFlow(imageCarousel)

    override fun onPageChanged(page: ImageCarouselPage) {
        imageCarousel.update { it.copy(currentImagePosition = page) }
    }
}