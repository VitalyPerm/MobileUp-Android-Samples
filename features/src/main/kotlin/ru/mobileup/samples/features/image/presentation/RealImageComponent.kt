package ru.mobileup.samples.features.image.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.utils.safePush
import ru.mobileup.samples.core.utils.toStateFlow
import ru.mobileup.samples.features.image.createImageCarouselComponent
import ru.mobileup.samples.features.image.createImageViewerComponent
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.presentation.carousel.ImageCarouselComponent

class RealImageComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, ImageComponent {

    private val navigation = StackNavigation<ChildConfig>()

    override val childStack: StateFlow<ChildStack<*, ImageComponent.Child>> = childStack(
        source = navigation,
        serializer = ChildConfig.serializer(),
        initialConfiguration = ChildConfig.Carousel,
        handleBackButton = true,
        childFactory = ::createChild
    ).toStateFlow(lifecycle)

    private fun createChild(
        childConfig: ChildConfig,
        componentContext: ComponentContext
    ): ImageComponent.Child = when (childConfig) {
        ChildConfig.Carousel -> ImageComponent.Child.Carousel(
            componentFactory.createImageCarouselComponent(
                componentContext,
                ::onCarouselOutput
            )
        )

        is ChildConfig.Viewer -> ImageComponent.Child.Viewer(
            componentFactory.createImageViewerComponent(
                componentContext,
                childConfig.imageCarousel
            )
        )
    }

    fun onCarouselOutput(output: ImageCarouselComponent.Output) {
        when (output) {
            is ImageCarouselComponent.Output.ViewerRequested -> {
                navigation.safePush(ChildConfig.Viewer(output.imageCarousel))
            }
        }
    }

    @Serializable
    sealed interface ChildConfig {

        @Serializable
        data object Carousel : ChildConfig

        @Serializable
        data class Viewer(val imageCarousel: ImageCarousel) : ChildConfig
    }
}