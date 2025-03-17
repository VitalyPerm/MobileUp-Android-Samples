package ru.mobileup.samples.features.collapsing_toolbar

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.collapsing_toolbar.presentation.CollapsingToolbarComponent
import ru.mobileup.samples.features.collapsing_toolbar.presentation.RealCollapsingToolbarComponent
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.CollapsingToolbarMainComponent
import ru.mobileup.samples.features.collapsing_toolbar.presentation.main.RealCollapsingToolbarMainComponent

fun ComponentFactory.createCollapsingToolbarComponent(
    componentContext: ComponentContext
): CollapsingToolbarComponent = RealCollapsingToolbarComponent(componentContext, get())

fun ComponentFactory.createCollapsingToolbarMainComponent(
    componentContext: ComponentContext,
    onOutput: (CollapsingToolbarMainComponent.Output) -> Unit
): CollapsingToolbarMainComponent = RealCollapsingToolbarMainComponent(componentContext, onOutput)
