package ru.mobileup.samples.features.qr_code.presentation.scanner

import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import ru.mobileup.samples.features.qr_code.domain.QrCode

@Composable
fun CameraPreviewWithQrCodeScanner(
    qrCodeScannerEnabled: Boolean,
    onQrCodeScan: (QrCode) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQrCodeScannerEnabled by rememberUpdatedState(qrCodeScannerEnabled)
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    val executor = ContextCompat.getMainExecutor(LocalContext.current)
    val barcodeScanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }

    Box(modifier) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { _ ->
                cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
                    if (currentQrCodeScannerEnabled) {
                        processImage(imageProxy, barcodeScanner, onQrCodeScan)
                    } else {
                        imageProxy.close()
                    }
                }
                previewView
            }

        )
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImage(
    imageProxy: ImageProxy,
    scanner: BarcodeScanner,
    onScanned: (QrCode) -> Unit
) {
    val image = imageProxy.image ?: return
    val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            val barcodeValue = barcodes.getOrNull(0)?.rawValue
            if (barcodeValue != null) {
                onScanned(QrCode(barcodeValue))
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}