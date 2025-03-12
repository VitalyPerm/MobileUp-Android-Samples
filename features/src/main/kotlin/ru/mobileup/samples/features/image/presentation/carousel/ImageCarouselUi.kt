package ru.mobileup.samples.features.image.presentation.carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.dispatchOnBackPressed
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.image.domain.ImageCarousel
import ru.mobileup.samples.features.image.domain.ImageCarouselPage

@Composable
fun ImageCarouselUi(
    component: ImageCarouselComponent,
    modifier: Modifier = Modifier
) {
    val imageCarousel by component.imageCarousel.collectAsState()

    Box(
        modifier = modifier
    ) {
        ImageCarouselContent(
            imageCarousel = imageCarousel,
            onCarouselPageChange = component::onCarouselPageChanged,
            onImageClick = component::onImageClick,
        )
        ImageCarouselToolbar(modifier = Modifier.systemBarsPadding())
    }
}

@Composable
private fun ImageCarouselToolbar(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxHeight()) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clickable { dispatchOnBackPressed(context) },
            shape = RoundedCornerShape(12.dp),
            color = CustomTheme.colors.background.screen
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.requiredSize(24.dp)
            )
        }
    }
}

@Composable
fun ImageCarouselContent(
    imageCarousel: ImageCarousel,
    onCarouselPageChange: (ImageCarouselPage) -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        pageCount = { imageCarousel.imageUrls.size }
    )

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Box {
            ImagePager(
                pagerState = pagerState,
                imageCarousel = imageCarousel,
                onPageChange = { onCarouselPageChange(it) },
                onImageClick = onImageClick
            )

            ImageOverlay(
                modifier = Modifier.matchParentSize(),
                imagesCount = imageCarousel.imageUrls.size,
                pagerState = pagerState,
            )
        }

        Text(
            text = stringResource(id = R.string.image_carousel_title_cats),
            style = CustomTheme.typography.title.regular,
            color = CustomTheme.colors.text.primary,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = stringResource(id = R.string.image_carousel_description_cats),
            style = CustomTheme.typography.body.regular,
            color = CustomTheme.colors.text.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun PageIndicator(
    imagesCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    defaultColor: Color = CustomTheme.colors.background.screen
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(imagesCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) defaultColor else defaultColor.copy(alpha = 0.6f)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }
}

@Composable
private fun ImagePager(
    pagerState: PagerState,
    imageCarousel: ImageCarousel,
    onPageChange: (ImageCarouselPage) -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(pagerState, onPageChange) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPageChange(ImageCarouselPage(page))
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) {
        val imageUrl = imageCarousel.imageUrls.getOrNull(it)
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8333f)
                .clickable(onClick = onImageClick),
            model = imageUrl?.value,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ImageOverlay(
    imagesCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PageIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            imagesCount = imagesCount,
            pagerState = pagerState
        )
    }
}

@Preview
@Composable
private fun ImageCarouselUiPreview() {
    AppTheme {
        ImageCarouselUi(FakeImageCarouselComponent())
    }
}