package ru.mobileup.samples.features.qr_code.presentation.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mobileup.samples.core.theme.AppTheme
import ru.mobileup.samples.core.theme.custom.CustomTheme
import ru.mobileup.samples.core.widget.FullscreenCircularProgress
import ru.mobileup.samples.core.widget.button.AppButton
import ru.mobileup.samples.core.widget.button.ButtonType
import ru.mobileup.samples.features.R

@Composable
fun QrCodeScannerUi(
    component: QrCodeScannerComponent,
    modifier: Modifier = Modifier
) {
    val state by component.scanningState.collectAsState()
    val scannedValue by component.currentScannedUrl.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CameraPreviewWithQrCodeScanner(
                qrCodeScannerEnabled = state is QrCodeScannerComponent.ScanningState.Scanning,
                onQrCodeScan = component::onQrCodeScanned,
                modifier = Modifier.matchParentSize()
            )

            when (state) {
                QrCodeScannerComponent.ScanningState.Scanning,
                QrCodeScannerComponent.ScanningState.RequestingPermission -> Unit

                QrCodeScannerComponent.ScanningState.PermissionDenied -> {
                    AppButton(
                        buttonType = ButtonType.Primary,
                        onClick = component::onGrantPermissionClick,
                        text = stringResource(R.string.qr_code_grant_permission),
                    )
                }

                QrCodeScannerComponent.ScanningState.Processing -> {
                    FullscreenCircularProgress()
                }
            }
        }

        Text(
            text = scannedValue ?: stringResource(R.string.qr_code_no_scanned_value),
            color = CustomTheme.colors.text.primary,
            style = CustomTheme.typography.caption.regular
        )
    }
}

@Preview
@Composable
private fun QrCodeScannerUiPreview() {
    AppTheme {
        QrCodeScannerUi(FakeQrCodeScannerComponent())
    }
}
