package ru.mobileup.samples.features.shared_element_transitions.presentation.details

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.widget.LocalSharedScope
import ru.mobileup.samples.core.widget.sharedScopeModifier
import ru.mobileup.samples.features.shared_element_transitions.presentation.widgets.SharedKeys

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailsSharedElementsUi(
    component: DetailsSharedElementsComponent,
    modifier: Modifier = Modifier,
) {
    val localSharedScope = LocalSharedScope.current

    Scaffold(
        modifier = modifier
            .then(
                localSharedScope.sharedScopeModifier { animScope ->
                    Modifier.sharedElement(
                        rememberSharedContentState(key = SharedKeys.surface(component.item.id)),
                        animatedVisibilityScope = animScope,
                        renderInOverlayDuringTransition = false,
                    )
                }
            ),
        contentWindowInsets = WindowInsets.statusBars
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .background(Color.White)
                    .then(
                        localSharedScope.sharedScopeModifier { animScope ->
                            Modifier.sharedBounds(
                                rememberSharedContentState(key = SharedKeys.image(component.item.id)),
                                animatedVisibilityScope = animScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = 0.8f,
                                        stiffness = 380f
                                    )
                                }
                            )
                        }
                    ),
                model = component.item.image.value,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )

            Text(
                modifier = Modifier
                    .then(
                        localSharedScope.sharedScopeModifier { animScope ->
                            Modifier.sharedBounds(
                                rememberSharedContentState(key = SharedKeys.title(component.item.id)),
                                animatedVisibilityScope = animScope,
                                zIndexInOverlay = 1f
                            )
                        }
                    ),
                text = component.item.title,
                style = CustomTheme.typography.title.regular,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                Text(
                    modifier = Modifier
                        .then(
                            localSharedScope.sharedScopeModifier { animScope ->
                                Modifier.sharedBounds(
                                    rememberSharedContentState(key = SharedKeys.text(component.item.id)),
                                    animatedVisibilityScope = animScope,
                                )
                            }
                        ),
                    text = component.item.text,
                    style = CustomTheme.typography.body.regular,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDetailsSharedElementsUi() {
    AppTheme {
        DetailsSharedElementsUi(
            component = FakeDetailsSharedElementsComponent(),
        )
    }
}