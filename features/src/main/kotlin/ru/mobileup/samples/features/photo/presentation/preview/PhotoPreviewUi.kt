package ru.mobileup.samples.features.photo.presentation.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil3.compose.rememberAsyncImagePainter
import ru.mobileup.samples.core.dialog.standard.StandardDialog
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.utils.SystemBars
import ru.mobileup.samples.features.R

@Composable
fun PhotoPreviewUi(
    component: PhotoPreviewComponent,
    modifier: Modifier = Modifier
) {
    SystemBars(transparentNavigationBar = true)

    StandardDialog(component.saveDialog)

    PhotoPreviewContent(
        component = component,
        modifier = modifier
    )
}

@Composable
private fun PhotoPreviewContent(
    component: PhotoPreviewComponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomTheme.colors.palette.grayscale.l900)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CustomTheme.colors.palette.black)
                .padding(horizontal = 8.dp, vertical = 24.dp)
                .padding(top = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(R.string.menu_item_preview),
                color = CustomTheme.colors.text.invert,
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = "download",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        component.onSaveClick()
                    }
            )
        }

        Image(
            painter = rememberAsyncImagePainter(component.media),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@UnstableApi
@Preview
@Composable
private fun PhotoPreviewUiPreview() {
    AppTheme {
        PhotoPreviewUi(FakePhotoPreviewComponent())
    }
}