package ru.mobileup.samples.features.tutorial.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.tutorial.presentation.overlay.highlightableItem
import ru.mobileup.samples.core.utils.dispatchOnBackPressed
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.tutorial.domain.TutorialFilter

@Composable
fun TutorialSampleUi(
    component: TutorialSampleComponent,
    modifier: Modifier = Modifier,
) {
    val items by component.items.collectAsState()
    val selectedFilter by component.selectedFilter.collectAsState()
    val availableFilters by component.availableFilters.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(modifier = Modifier.padding(horizontal = 4.dp))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(top = 8.dp + paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltersSection(
                modifier = Modifier.padding(horizontal = 20.dp),
                availableFilters = availableFilters,
                selectedFilter = selectedFilter,
                onFilterSelect = component::onFilterSelected
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 20.dp)
            ) {
                items(items) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = CustomTheme.colors.button.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        text = it.toString(),
                        style = CustomTheme.typography.button.bold,
                        color = CustomTheme.colors.text.invert
                    )
                }
                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    }
}

@Composable
fun FiltersSection(
    availableFilters: List<TutorialFilter>,
    selectedFilter: TutorialFilter,
    onFilterSelect: (TutorialFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableFilters.forEach { filter ->
            val containerColor by animateColorAsState(
                if (filter == selectedFilter) {
                    CustomTheme.colors.text.primary
                } else {
                    CustomTheme.colors.text.invert
                }
            )
            val textColor by animateColorAsState(
                if (filter == selectedFilter) {
                    CustomTheme.colors.text.invert
                } else {
                    CustomTheme.colors.text.primary
                }
            )

            BasicText(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .drawBehind { drawRect(containerColor) }
                    .clickable { onFilterSelect(filter) }
                    .highlightableItem(
                        key = filter.toTutorialMsssageKey(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .minimumInteractiveComponentSize()
                    .padding(12.dp),
                text = filter.displayName.localized(),
                style = CustomTheme.typography.button.bold.copy(
                    textAlign = TextAlign.Center
                ),
                color = { textColor }
            )
        }
    }
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        IconButton(
            onClick = { dispatchOnBackPressed(context) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .highlightableItem(
                    key = TutorialMessageKeys.Back,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null
            )
        }

        Text(
            text = stringResource(id = R.string.tutorial_toolbar_title),
            style = CustomTheme.typography.title.regular,
            color = CustomTheme.colors.text.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .highlightableItem(
                    key = TutorialMessageKeys.Title,
                    shape = RectangleShape
                )
                .padding(8.dp)
        )
    }
}

val TutorialFilter.displayName: StringDesc
    get() = when (this) {
        TutorialFilter.All -> R.string.tutorial_filter_all.strResDesc()
        TutorialFilter.First -> R.string.tutorial_filter_first.strResDesc()
        TutorialFilter.Second -> R.string.tutorial_filter_second.strResDesc()
        TutorialFilter.Third -> R.string.tutorial_filter_third.strResDesc()
    }

fun TutorialFilter.toTutorialMsssageKey() = when (this) {
    TutorialFilter.All -> TutorialMessageKeys.All
    TutorialFilter.First -> TutorialMessageKeys.First
    TutorialFilter.Second -> TutorialMessageKeys.Second
    TutorialFilter.Third -> TutorialMessageKeys.Third
}
