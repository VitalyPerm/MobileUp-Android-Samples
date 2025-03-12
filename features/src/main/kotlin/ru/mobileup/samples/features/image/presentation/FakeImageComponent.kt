package ru.mobileup.samples.features.image.presentation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.image.presentation.carousel.FakeImageCarouselComponent

class FakeImageComponent : ImageComponent {
    override val childStack: StateFlow<ChildStack<*, ImageComponent.Child>> =
        createFakeChildStackStateFlow(
            ImageComponent.Child.Carousel(FakeImageCarouselComponent())
        )
}