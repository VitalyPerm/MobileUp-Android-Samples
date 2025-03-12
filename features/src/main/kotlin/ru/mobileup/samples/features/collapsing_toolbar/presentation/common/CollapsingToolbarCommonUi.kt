package ru.mobileup.samples.features.collapsing_toolbar.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.dispatchOnBackPressed
import ru.mobileup.samples.core.widget.text_field.AppTextField
import ru.mobileup.samples.features.collapsing_toolbar.presentation.common.widget.CollapsingTitle
import ru.mobileup.samples.features.collapsing_toolbar.presentation.common.widget.CustomToolbar
import ru.mobileup.samples.features.collapsing_toolbar.presentation.common.widget.CustomToolbarDefaults

@Composable
fun CollapsingToolbarCommonUi(
    modifier: Modifier = Modifier,
) {
    val configState = rememberSaveable(saver = ToolbarConfig.Saver) {
        mutableStateOf(ToolbarConfig())
    }

    val scrollBehavior = CustomToolbarDefaults.customToolbarScrollBehavior()

    var dynamicTitle by remember { mutableStateOf("Title") }

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .imePadding(),
        topBar = {
            CustomToolbar(
                navigationIcon = navigationIcon(configState.value.navigationContent),
                actions = actions(configState.value.actionsContent),
                collapsingTitle = collapsingTitle(configState.value.titleContent, dynamicTitle),
                topContent = topContent(configState.value.topContent),
                bottomContent = bottomContent(configState.value.bottomContent),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            titleContent(configState, dynamicTitle) { dynamicTitle = it }
            navigationContent(configState)
            actionsContent(configState)
            topContent(configState)
            bottomContent(configState)
            stubs()
            item("spacer") { Spacer(Modifier.height(8.dp)) }
        }
    }
}

private fun navigationIcon(
    navigationContent: NavigationContent,
): (@Composable BoxScope.() -> Unit)? = when (navigationContent) {
    NavigationContent.BackArrow -> {
        {
            val context = LocalContext.current
            MaterialClickableIcon(Icons.AutoMirrored.Filled.ArrowBack) {
                dispatchOnBackPressed(context)
            }
        }
    }

    NavigationContent.None -> null
}

private fun actions(
    actionsContent: ActionsContent,
): (@Composable RowScope.() -> Unit)? = when (actionsContent) {
    ActionsContent.Icon -> {
        {
            MaterialClickableIcon(Icons.Filled.MoreVert)
        }
    }

    ActionsContent.Icons -> {
        {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MaterialClickableIcon(Icons.Outlined.Info)
                MaterialClickableIcon(Icons.Filled.Settings)
                MaterialClickableIcon(Icons.Filled.MoreVert)
            }
        }
    }

    ActionsContent.Text -> {
        {
            TextButton({}) {
                Text(text = "Button", style = CustomTheme.typography.button.bold)
            }
        }
    }

    ActionsContent.None -> null
}

private fun topContent(
    topContent: TopContent,
): (@Composable BoxScope.() -> Unit)? = when (topContent) {
    TopContent.ProgressBar -> {
        {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
    }

    TopContent.Title -> {
        {
            Text(text = "Top content title", style = MaterialTheme.typography.titleLarge)
        }
    }

    TopContent.TitleSubtitle -> {
        {
            Column {
                Text(
                    text = "Top content title long long",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Top content subtitle", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    TopContent.None -> null
}

@Composable
private fun collapsingTitle(
    titleContent: TitleContent,
    title: String = "",
): CollapsingTitle? = when (titleContent) {
    TitleContent.SingleLine -> CollapsingTitle(
        "Title",
        MaterialTheme.typography.headlineLarge
    )

    TitleContent.Multiline -> CollapsingTitle(
        "Title with large multiline text second line",
        MaterialTheme.typography.headlineLarge
    )

    TitleContent.Dynamic -> CollapsingTitle(
        title,
        MaterialTheme.typography.headlineLarge
    )

    TitleContent.None -> null
}

private fun bottomContent(
    bottomContent: BottomContent,
): (@Composable BoxScope.() -> Unit)? = when (bottomContent) {
    BottomContent.Chips -> {
        {
            var selectedChip by remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                        FilterChip(
                            onClick = { selectedChip = index },
                            selected = selectedChip == index,
                            label = { Text("Chip $index") },
                            shape = CircleShape
                        )
                    }
                }
            }
        }
    }

    BottomContent.None -> null
}

@Composable
private fun ConfigRadioButton(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ConfigTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(16.dp),
        text = text,
        style = MaterialTheme.typography.titleSmall
    )
}

private fun LazyListScope.titleContent(
    toolbarConfigState: MutableState<ToolbarConfig>,
    dynamicTitle: String,
    onDynamicTitleChange: (String) -> Unit,
) {
    item("collapsing_title_config") {
        ConfigTitle("Collapsing title")
        TitleContent.entries.forEach { titleMode ->
            toolbarConfigState.run {
                ConfigRadioButton(
                    name = titleMode.name,
                    isSelected = value.titleContent == titleMode,
                    onClick = { value = value.copy(titleContent = titleMode) }
                )
            }
        }
    }
    if (toolbarConfigState.value.titleContent == TitleContent.Dynamic) {
        item("dynamic_title_text_field") {
            AppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 16.dp),
                text = dynamicTitle,
                onTextChange = onDynamicTitleChange
            )
        }
    }
}

private fun LazyListScope.navigationContent(toolbarConfigState: MutableState<ToolbarConfig>) {
    item("back_navigation_config") {
        ConfigTitle("Navigation slot content")
        NavigationContent.entries.forEach { navMode ->
            toolbarConfigState.run {
                ConfigRadioButton(
                    name = navMode.name,
                    isSelected = value.navigationContent == navMode,
                    onClick = { value = value.copy(navigationContent = navMode) }
                )
            }
        }
    }
}

private fun LazyListScope.actionsContent(toolbarConfigState: MutableState<ToolbarConfig>) {
    item("actions_config") {
        ConfigTitle("Actions content")
        ActionsContent.entries.forEach { action ->
            toolbarConfigState.run {
                ConfigRadioButton(
                    name = action.name,
                    isSelected = value.actionsContent == action,
                    onClick = { value = value.copy(actionsContent = action) }
                )
            }
        }
    }
}

private fun LazyListScope.topContent(toolbarConfigState: MutableState<ToolbarConfig>) {
    item("central_content_config") {
        ConfigTitle("Top content")
        TopContent.entries.forEach { contentMode ->
            toolbarConfigState.run {
                ConfigRadioButton(
                    name = contentMode.name,
                    isSelected = value.topContent == contentMode,
                    onClick = { value = value.copy(topContent = contentMode) }
                )
            }
        }
    }
}

private fun LazyListScope.bottomContent(toolbarConfigState: MutableState<ToolbarConfig>) {
    item("additional_content_config") {
        ConfigTitle("Bottom content")
        BottomContent.entries.forEach { bottomMode ->
            toolbarConfigState.run {
                ConfigRadioButton(
                    name = bottomMode.name,
                    isSelected = value.bottomContent == bottomMode,
                    onClick = { value = value.copy(bottomContent = bottomMode) }
                )
            }
        }
    }
}

private fun LazyListScope.stubs() {
    items(10, { "stub_$it" }) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CustomTheme.colors.background.secondary)
        )
    }
}

@Composable
private fun MaterialClickableIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    IconButton(onClick, modifier) {
        Icon(imageVector, null)
    }
}

private enum class NavigationContent {
    BackArrow, None
}

private enum class ActionsContent {
    Icon, Icons, Text, None
}

private enum class TopContent {
    ProgressBar, Title, TitleSubtitle, None
}

private enum class TitleContent {
    SingleLine, Multiline, Dynamic, None
}

private enum class BottomContent {
    Chips, None
}

@Serializable
private data class ToolbarConfig(
    val titleContent: TitleContent = TitleContent.SingleLine,
    val navigationContent: NavigationContent = NavigationContent.BackArrow,
    val actionsContent: ActionsContent = ActionsContent.Icon,
    val topContent: TopContent = TopContent.None,
    val bottomContent: BottomContent = BottomContent.None,
) {
    companion object {
        val Saver: Saver<MutableState<ToolbarConfig>, *> = Saver(
            save = {
                Json.encodeToString(serializer(), it.value)
            },
            restore = Json::decodeFromString
        )
    }
}

@Preview
@Composable
private fun CollapsingToolbarCommonPreview() {
    AppTheme {
        CollapsingToolbarCommonUi()
    }
}
