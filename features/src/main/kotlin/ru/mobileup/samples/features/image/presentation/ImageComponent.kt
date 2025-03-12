package ru.mobileup.samples.features.image.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.presentation.carousel.ImageCarouselComponent
import ru.mobileup.samples.features.image.presentation.viewer.ImageViewerComponent

interface ImageComponent {

    val childStack: StateFlow<ChildStack<*, Child>>

    sealed interface Child {
        data class Carousel(val component: ImageCarouselComponent) : Child
        data class Viewer(val component: ImageViewerComponent) : Child
    }
}