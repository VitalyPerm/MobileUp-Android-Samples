package ru.mobileup.samples.features.image

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.presentation.RealImageComponent
import ru.mobileup.samples.features.image.presentation.carousel.ImageCarouselComponent
import ru.mobileup.samples.features.image.presentation.carousel.RealImageCarouselComponent
import ru.mobileup.samples.features.image.presentation.viewer.RealImageViewerComponent

fun ComponentFactory.createImageComponent(
    componentContext: ComponentContext
) = RealImageComponent(componentContext, get())

fun ComponentFactory.createImageCarouselComponent(
    componentContext: ComponentContext,
    onOutput: (ImageCarouselComponent.Output) -> Unit
) = RealImageCarouselComponent(componentContext, onOutput)

fun ComponentFactory.createImageViewerComponent(
    componentContext: ComponentContext,
    imageCarousel: ImageCarousel
) = RealImageViewerComponent(componentContext, imageCarousel)