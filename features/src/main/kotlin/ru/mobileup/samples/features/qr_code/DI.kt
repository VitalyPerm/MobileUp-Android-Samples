package ru.mobileup.samples.features.qr_code

import com.arkivanov.decompose.ComponentContext
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.qr_code.presentation.generator.QrCodeGeneratorComponent
import ru.mobileup.samples.features.qr_code.presentation.generator.RealQrCodeGeneratorComponent
import ru.mobileup.samples.features.qr_code.presentation.scanner.QrCodeScannerComponent
import ru.mobileup.samples.features.qr_code.presentation.scanner.RealQrCodeScannerComponent
import org.koin.core.component.get

fun ComponentFactory.createQrCodeGeneratorComponent(
    componentContext: ComponentContext
): QrCodeGeneratorComponent {
    return RealQrCodeGeneratorComponent(componentContext)
}

fun ComponentFactory.createQrCodeScannerComponent(
    componentContext: ComponentContext
): QrCodeScannerComponent {
    return RealQrCodeScannerComponent(componentContext, get(), get(), get())
}