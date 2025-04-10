package ru.mobileup.samples.features.qr_code.presentation

import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.core.utils.fakeBackHandler
import ru.mobileup.samples.features.qr_code.presentation.QrCodeComponent.Child
import ru.mobileup.samples.features.qr_code.presentation.generator.FakeQrCodeGeneratorComponent

class FakeQrCodeComponent : QrCodeComponent {

    override val stack = createFakeChildStackStateFlow(
        Child.Generator(FakeQrCodeGeneratorComponent())
    )

    override val backHandler = fakeBackHandler

    override fun onBackClick() = Unit
}
