package ru.mobileup.samples.features.qr_code.presentation

import androidx.activity.OnBackPressedDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import ru.mobileup.samples.core.utils.createFakeChildStackStateFlow
import ru.mobileup.samples.features.qr_code.presentation.QrCodeComponent.Child
import ru.mobileup.samples.features.qr_code.presentation.generator.FakeQrCodeGeneratorComponent

class FakeQrCodeComponent : QrCodeComponent {

    override val stack = createFakeChildStackStateFlow(
        Child.Generator(FakeQrCodeGeneratorComponent())
    )

    override val backHandler = BackHandler(OnBackPressedDispatcher())

    override fun onBackClick() = Unit
}
