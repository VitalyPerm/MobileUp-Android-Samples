package ru.mobileup.samples.features.form

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.form.presentation.FormComponent
import ru.mobileup.samples.features.form.presentation.RealFormComponent

fun ComponentFactory.createFormComponent(
    componentContext: ComponentContext
): FormComponent {
    return RealFormComponent(componentContext, get(), get(), get())
}