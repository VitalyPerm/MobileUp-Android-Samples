package ru.mobileup.samples.features.shared_element_transitions.presentation.widgets

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.widget.LocalSharedScope
import ru.mobileup.samples.core.widget.sharedScopeModifier
import ru.mobileup.samples.features.shared_element_transitions.data.ItemSharedElementItems
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ItemSharedElementUi(
    item: ItemSharedElement,
    onClick: (ItemSharedElement) -> Unit,
    modifier: Modifier = Modifier,
) {
    val localSharedScope = LocalSharedScope.current

    val context = LocalContext.current

    Surface(
        modifier = modifier
            .then(
                localSharedScope.sharedScopeModifier { animScope ->
                    Modifier.sharedBounds(
                        rememberSharedContentState(key = SharedKeys.surface(item.id)),
                        animatedVisibilityScope = animScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    )
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = CustomTheme.colors.background.screen,
        shadowElevation = 4.dp,
        onClick = { onClick(item) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(75.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .align(Alignment.Top)
                    .then(
                        localSharedScope.sharedScopeModifier { animScope ->
                            Modifier.sharedElement(
                                rememberSharedContentState(key = SharedKeys.image(item.id)),
                                animatedVisibilityScope = animScope,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(16.dp)),
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = 0.8f,
                                        stiffness = 380f
                                    )
                                },
                            )
                        }
                    ),
                model = ImageRequest.Builder(context)
                    .data(item.image.value)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Column {
                Text(
                    modifier = Modifier
                        .then(
                            localSharedScope.sharedScopeModifier { animScope ->
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = SharedKeys.title(item.id)),
                                    animatedVisibilityScope = animScope,
                                )
                            }
                        ),
                    text = item.title,
                    style = CustomTheme.typography.title.regular,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .then(
                            localSharedScope.sharedScopeModifier { animScope ->
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = SharedKeys.text(item.id)),
                                    animatedVisibilityScope = animScope,
                                )
                            }
                        ),
                    text = item.text,
                    style = CustomTheme.typography.body.regular,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewItemSharedElementUi() {
    AppTheme {
        ItemSharedElementUi(
            item = ItemSharedElementItems.items.first(),
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}