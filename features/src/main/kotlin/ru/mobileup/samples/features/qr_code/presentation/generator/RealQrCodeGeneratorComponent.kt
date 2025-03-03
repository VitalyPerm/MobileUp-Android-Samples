package ru.mobileup.samples.features.qr_code.presentation.generator

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.features.qr_code.domain.QrCode

class RealQrCodeGeneratorComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, QrCodeGeneratorComponent {

    companion object {
        private const val LINK = "https://mobileup.ru/"
    }

    override val link = MutableStateFlow(QrCode(LINK))
}