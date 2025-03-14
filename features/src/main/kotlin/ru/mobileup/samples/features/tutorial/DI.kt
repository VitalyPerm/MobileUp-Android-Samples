package ru.mobileup.samples.features.tutorial

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import ru.mobileup.samples.features.tutorial.presentation.RealTutorialSampleComponent
import org.koin.dsl.module
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.core.tutorial.data.TutorialStatusStorage
import ru.mobileup.samples.core.tutorial.data.TutorialStatusStorageImpl
import ru.mobileup.samples.core.tutorial.domain.TutorialManager
import ru.mobileup.samples.core.tutorial.domain.TutorialManagerImpl

val tutorialModule = module {
    single<TutorialStatusStorage> { TutorialStatusStorageImpl(get()) }
    single<TutorialManager> { TutorialManagerImpl() }
}

fun ComponentFactory.createTutorialSampleComponent(
    componentContext: ComponentContext
) = RealTutorialSampleComponent(componentContext, get())