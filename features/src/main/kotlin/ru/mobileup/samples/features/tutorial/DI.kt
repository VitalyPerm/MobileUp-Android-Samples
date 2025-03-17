package ru.mobileup.samples.features.tutorial

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.tutorial.presentation.RealTutorialSampleComponent

fun ComponentFactory.createTutorialSampleComponent(
    componentContext: ComponentContext
) = RealTutorialSampleComponent(componentContext, get())