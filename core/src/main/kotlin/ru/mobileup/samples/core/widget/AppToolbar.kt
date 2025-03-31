package ru.mobileup.samples.core.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.dispatchOnBackPressed

@Composable
fun AppToolbar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = {
        val context = LocalContext.current
        IconButton(onClick = { dispatchOnBackPressed(context) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null
            )
        }
    },
    actionIcon: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current
    Box(modifier = modifier) {
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            navigationIcon?.invoke()
        }

        title?.let {
            Text(
                text = it,
                style = CustomTheme.typography.title.regular,
                color = CustomTheme.colors.text.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            actionIcon?.invoke()
        }
    }
}