package ru.mobileup.samples.features.qr_code.presentation.scanner

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.qr_code.domain.QrCode
import ru.mobileup.samples.features.qr_code.presentation.scanner.QrCodeScannerComponent.ScanningState

class FakeQrCodeScannerComponent : QrCodeScannerComponent {
    override val scanningState: StateFlow<ScanningState> = MutableStateFlow(ScanningState.Processing)

    override val currentScannedUrl: StateFlow<String?> = MutableStateFlow("")

    override fun onQrCodeScanned(qrCode: QrCode): Unit = Unit

    override fun onQrErrorClosed(): Unit = Unit

    override fun onGrantPermissionClick(): Unit = Unit
}
