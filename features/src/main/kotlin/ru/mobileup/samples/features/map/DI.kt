package ru.mobileup.samples.features.map

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.get
import org.koin.dsl.module
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.features.map.data.MapRepository
import ru.mobileup.samples.features.map.data.MapRepositoryImpl
import ru.mobileup.samples.core.map.domain.MapVendor
import ru.mobileup.samples.features.map.presentation.MapComponent
import ru.mobileup.samples.features.map.presentation.RealMapComponent
import ru.mobileup.samples.features.map.presentation.main.MapMainComponent
import ru.mobileup.samples.features.map.presentation.main.RealMapMainComponent
import ru.mobileup.samples.features.map.presentation.type.MapVendorComponent
import ru.mobileup.samples.features.map.presentation.type.RealMapVendorComponent

val mapModule = module {
    single<MapRepository> { MapRepositoryImpl(get()) }
}

fun ComponentFactory.createMapComponent(
    componentContext: ComponentContext,
    type: MapVendor
): MapComponent {
    return RealMapComponent(
        componentContext,
        type,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
    )
}

fun ComponentFactory.createMapMainComponent(
    componentContext: ComponentContext
): MapMainComponent = RealMapMainComponent(
    componentContext,
    get()
)

fun ComponentFactory.createMapVendorComponent(
    componentContext: ComponentContext,
    output: (MapVendorComponent.Output) -> Unit
): MapVendorComponent = RealMapVendorComponent(
    componentContext,
    output
)