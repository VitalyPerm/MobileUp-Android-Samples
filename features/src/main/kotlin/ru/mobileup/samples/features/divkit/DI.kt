package ru.mobileup.samples.features.divkit

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.divkit.data.DivKitRepository
import ru.mobileup.samples.features.divkit.data.DivKitRepositoryImpl
import ru.mobileup.samples.features.divkit.presentation.DivKitComponent
import ru.mobileup.samples.features.divkit.presentation.RealDivKitComponent
import ru.mobileup.samples.features.divkit.presentation.details.DivKitDetailsComponent
import ru.mobileup.samples.features.divkit.presentation.details.RealDivKitDetailsComponent
import ru.mobileup.samples.features.divkit.presentation.list.DivKitListComponent
import ru.mobileup.samples.features.divkit.presentation.list.RealDivKitListComponent

val divKitModule = module {
    single<DivKitRepository> { DivKitRepositoryImpl(get(), get()) }
}

fun ComponentFactory.createDivKitComponent(componentContext: ComponentContext): DivKitComponent =
    RealDivKitComponent(componentContext, get())

fun ComponentFactory.createDivKitListComponent(
    componentContext: ComponentContext,
    output: (DivKitListComponent.Output) -> Unit
): DivKitListComponent = RealDivKitListComponent(
    componentContext,
    get(),
    output,
    get(),
    get()
)

fun ComponentFactory.createDivKitDetailsComponent(
    componentContext: ComponentContext,
    title: String,
    jsonName: String
): DivKitDetailsComponent = RealDivKitDetailsComponent(
    componentContext,
    get(),
    title,
    jsonName,
    get(),
    get(),
    get(),
)