package ru.mobileup.samples.features.qr_code.presentation.generator

import kotlinx.coroutines.flow.StateFlow
import ru.mobileup.samples.features.qr_code.domain.QrCode

interface QrCodeGeneratorComponent {
    val link: StateFlow<QrCode>
}