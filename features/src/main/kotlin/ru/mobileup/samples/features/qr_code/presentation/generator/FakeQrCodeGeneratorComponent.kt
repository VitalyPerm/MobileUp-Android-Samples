package ru.mobileup.samples.features.qr_code.presentation.generator

import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.features.qr_code.domain.QrCode

class FakeQrCodeGeneratorComponent : QrCodeGeneratorComponent {
    override val link = MutableStateFlow(QrCode("https://mobileup.ru/"))
}
