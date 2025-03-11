package ru.mobileup.samples.features.qr_code.presentation.scanner

import android.Manifest
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.mobileup.samples.core.error_handling.ErrorHandler
import ru.mobileup.samples.core.error_handling.safeLaunch
import ru.mobileup.samples.core.error_handling.safeRun
import ru.mobileup.samples.core.external_apps.data.ExternalAppService
import ru.mobileup.samples.core.permissions.PermissionService
import ru.mobileup.samples.core.permissions.SinglePermissionResult
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.qr_code.domain.QrCode

class RealQrCodeScannerComponent(
    componentContext: ComponentContext,
    private val errorHandler: ErrorHandler,
    private val externalAppService: ExternalAppService,
    private val permissionService: PermissionService
) : ComponentContext by componentContext, QrCodeScannerComponent {

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }

    init {
        doOnStart {
            checkPermission()
        }
    }

    override val scanningState = MutableStateFlow<QrCodeScannerComponent.ScanningState>(
        QrCodeScannerComponent.ScanningState.RequestingPermission
    )

    override val currentScannedUrl = MutableStateFlow<String?>(null)

    override fun onQrErrorClosed() {
        scanningState.value = QrCodeScannerComponent.ScanningState.Scanning
    }

    override fun onGrantPermissionClick() {
        safeRun(errorHandler) {
            externalAppService.openAppSettings()
        }
    }

    override fun onQrCodeScanned(qrCode: QrCode) = currentScannedUrl.update { qrCode.value }

    private fun checkPermission() {
        scanningState.value = QrCodeScannerComponent.ScanningState.RequestingPermission
        componentScope.safeLaunch(errorHandler) {
            when (permissionService.requestPermission(CAMERA_PERMISSION)) {
                SinglePermissionResult.Granted -> {
                    scanningState.value = QrCodeScannerComponent.ScanningState.Scanning
                }

                is SinglePermissionResult.Denied -> {
                    scanningState.value = QrCodeScannerComponent.ScanningState.PermissionDenied
                }
            }
        }
    }
}