package ru.mobileup.samples.features.settings.presentation.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.custom.CustomTheme

@Composable
fun <T> SettingsPicker(
    title: String,
    selectedOption: T,
    options: List<T>,
    onOptionSelect: (T) -> Unit,
    displayOptionMapper: (T) -> String,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val animateRotation by animateFloatAsState(if (isExpanded) -180f else 0f)

    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CustomTheme.colors.border.primary, RoundedCornerShape(16.dp))
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(displayOptionMapper(selectedOption))

            Spacer(Modifier.weight(1f))

            Icon(
                modifier = Modifier.graphicsLayer { rotationZ = animateRotation },
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        AnimatedVisibility(isExpanded) {
            Column {
                Spacer(Modifier.height(8.dp))

                options.forEach {
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayOptionMapper(it),
                                    style = CustomTheme.typography.body.regular
                                )

                                Spacer(Modifier.weight(1f))

                                if (it == selectedOption) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 32.dp),
                        onClick = {
                            onOptionSelect(it)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}
