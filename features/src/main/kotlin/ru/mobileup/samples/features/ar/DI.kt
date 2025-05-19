package ru.mobileup.samples.features.ar

import android.opengl.GLSurfaceView
import com.arkivanov.decompose.ComponentContext
import com.google.ar.core.Session
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import ru.mobileup.samples.core.ComponentFactory
import ru.mobileup.samples.feature.ar.core.helpers.ARDisplayRotationHelper
import ru.mobileup.samples.feature.ar.core.helpers.ARTapHelper
import ru.mobileup.samples.features.ar.core.helpers.ARDepthSettings
import ru.mobileup.samples.features.ar.core.helpers.ARInstantPlacementSettings
import ru.mobileup.samples.features.ar.core.helpers.ARSessionLifecycleHelper
import ru.mobileup.samples.features.ar.core.helpers.ARTrackingStateHelper
import ru.mobileup.samples.features.ar.core.render.ARRenderer
import ru.mobileup.samples.features.ar.presentation.ARComponent
import ru.mobileup.samples.features.ar.presentation.RealARComponent
import ru.mobileup.samples.features.ar.presentation.menu.ARMenuComponent
import ru.mobileup.samples.features.ar.presentation.menu.RealARMenuComponent
import ru.mobileup.samples.features.ar.presentation.placement.ARPlacementComponent
import ru.mobileup.samples.features.ar.presentation.placement.RealARPlacementComponent

val arModule = module {
    single { ARTapHelper(get()) }
    factory {
        val tapHelper = get<ARTapHelper>()
        GLSurfaceView(get()).apply {
            setOnTouchListener(tapHelper)
        }
    }
    single { ARDisplayRotationHelper(get()) }
    single { ARDepthSettings() }
    single { ARInstantPlacementSettings() }
    single { ARTrackingStateHelper(get()) }
    factory { ARSessionLifecycleHelper(get(), get()) }

    factory { (getSession: () -> Session?) ->
        ARRenderer(
            resourceProvider = get(),
            tapHelper = get(),
            displayRotationHelper = get(),
            messageService = get(),
            depthSettings = get(),
            instantPlacementSettings = get(),
            trackingStateHelper = get(),
            getSession = getSession,
        )
    }
}

fun ComponentFactory.createArComponent(
    componentContext: ComponentContext,
): ARComponent = RealARComponent(
    componentContext,
    get()
)

fun ComponentFactory.createArPlacementComponent(
    componentContext: ComponentContext,
): ARPlacementComponent = RealARPlacementComponent(
    componentContext,
    get(),
    get(),
    get(),
    get(),
    get(),
    arRenderFactory = { getSession ->
        get<ARRenderer> {
            parametersOf(getSession)
        }
    }
)

fun ComponentFactory.createArMenuComponent(
    componentContext: ComponentContext,
    output: (ARMenuComponent.Output) -> Unit
): ARMenuComponent = RealARMenuComponent(
    componentContext,
    get(),
    output
)