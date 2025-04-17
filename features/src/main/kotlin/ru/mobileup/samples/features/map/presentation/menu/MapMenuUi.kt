package ru.mobileup.samples.features.map.presentation.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.dialog.Dialog
import ru.mobileup.samples.core.map.domain.MapVendor
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.features.R

@Composable
fun MapMenuUi(
    component: MapMenuComponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            buttonType = ButtonType.Primary,
            text = stringResource(R.string.map_yandex),
            onClick = { component.onVendorSelected(MapVendor.Yandex) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        AppButton(
            buttonType = ButtonType.Primary,
            text = stringResource(R.string.map_google),
            onClick = { component.onVendorSelected(MapVendor.Google) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

    Dialog(component.apiKeyNotFoundDialogControl) {
        val key = when (it) {
            MapVendor.Yandex -> "yandex.map.api.key"
            MapVendor.Google -> "google.map.api.key"
        }
        Surface {
            Text(
                text = stringResource(R.string.map_api_key_not_found, key),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MapMenuPreview() {
    AppTheme {
        MapMenuUi(FakeMapMenuComponent())
    }
}