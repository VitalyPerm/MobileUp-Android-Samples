package ru.mobileup.samples.features.otp

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.otp.presentation.OtpComponent
import ru.mobileup.samples.features.otp.presentation.RealOtpComponent

fun ComponentFactory.createOtpComponent(
    componentContext: ComponentContext,
    onOutput: (OtpComponent.Output) -> Unit
): OtpComponent {
    return RealOtpComponent(componentContext, get(), onOutput)
}