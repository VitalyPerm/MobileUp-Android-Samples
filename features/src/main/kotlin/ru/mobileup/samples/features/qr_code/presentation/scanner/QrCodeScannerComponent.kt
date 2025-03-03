package ru.mobileup.samples.features.qr_code.presentation.scanner

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.qr_code.domain.QrCode

interface QrCodeScannerComponent {
    val scanningState: StateFlow<ScanningState>
    val currentScannedUrl: StateFlow<String?>

    fun onQrCodeScanned(qrCode: QrCode)
    fun onQrErrorClosed()
    fun onGrantPermissionClick()

    sealed interface ScanningState {
        data object RequestingPermission : ScanningState
        data object PermissionDenied : ScanningState
        data object Scanning : ScanningState
        data object Processing : ScanningState
    }
}