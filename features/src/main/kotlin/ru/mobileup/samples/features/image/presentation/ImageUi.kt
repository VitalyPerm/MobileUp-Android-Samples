package ru.mobileup.samples.features.image.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.stack.Children
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.features.image.presentation.carousel.ImageCarouselUi
import ru.mobileup.samples.features.image.presentation.viewer.ImageViewerUi

@Composable
fun ImageUi(
    component: ImageComponent,
    modifier: Modifier = Modifier
) {
    val childStack by component.childStack.collectAsState()

    Children(childStack, modifier) { child ->
        when (val instance = child.instance) {
            is ImageComponent.Child.Carousel -> ImageCarouselUi(instance.component)
            is ImageComponent.Child.Viewer -> ImageViewerUi(instance.component)
        }
    }
}

@Preview
@Composable
private fun ImageUiPreview() {
    AppTheme {
        ImageUi(FakeImageComponent())
    }
}