package ru.mobileup.samples.features.image.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.image.presentation.carousel.FakeImageCarouselComponent
import ru.mobileup.samples.features.image.presentation.carousel.ImageCarouselComponent

class FakeImageComponent : ImageComponent {
    override val imageCarouselComponent: ImageCarouselComponent = FakeImageCarouselComponent()
    override val title: StateFlow<StringDesc> = MutableStateFlow("Title".desc())
    override val description: StateFlow<StringDesc> = MutableStateFlow("Description".desc())
}