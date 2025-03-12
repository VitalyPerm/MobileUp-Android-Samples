package ru.mobileup.samples.features.image.presentation.carousel

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

class RealImageCarouselComponent(
    componentContext: ComponentContext,
    private val onOutput: (ImageCarouselComponent.Output) -> Unit
) : ComponentContext by componentContext, ImageCarouselComponent {

    override val imageCarousel: MutableStateFlow<ImageCarousel> = MutableStateFlow(ImageCarousel.MOCK)

    override fun onCarouselPageChanged(imageCarouselPage: ImageCarouselPage) {
        imageCarousel.update { it.copy(currentImagePosition = imageCarouselPage) }
    }

    override fun onImageClick() {
        onOutput(ImageCarouselComponent.Output.ViewerRequested(imageCarousel.value))
    }
}