package ru.mobileup.samples.features.ar.presentation.placement

import android.opengl.GLSurfaceView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.google.ar.core.Config
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.core.Session
import ru.mobileup.samples.core.error_handling.ErrorHandler
import ru.mobileup.samples.core.error_handling.safeLaunch
import ru.mobileup.samples.core.resource.ResourceProvider
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.feature.ar.core.entity.ARSampleRender
import ru.mobileup.samples.features.ar.core.helpers.ARInstantPlacementSettings
import ru.mobileup.samples.features.ar.core.helpers.ARSessionLifecycleHelper
import ru.mobileup.samples.features.ar.core.render.ARRenderer

class RealARPlacementComponent(
    componentContext: ComponentContext,
    resourceProvider: ResourceProvider,
    errorHandler: ErrorHandler,
    override val glSurfaceView: GLSurfaceView,
    arCoreSessionHelper: ARSessionLifecycleHelper,
    private val instantPlacementSettings: ARInstantPlacementSettings,
    arRenderFactory: (
        getSession: () -> Session?,
    ) -> ARRenderer
) : ARPlacementComponent, ComponentContext by componentContext {

    private lateinit var renderer: ARRenderer

    init {
        componentScope.safeLaunch(errorHandler) {
            arCoreSessionHelper.beforeSessionResume = ::configureSession
            renderer = arRenderFactory { arCoreSessionHelper.session }

            ARSampleRender(glSurfaceView, renderer, resourceProvider.assetsManager)
        }

        lifecycle.doOnResume {
            arCoreSessionHelper.onResume()
            glSurfaceView.onResume()
            renderer.onResume()
        }

        lifecycle.doOnPause {
            arCoreSessionHelper.onPause()
            glSurfaceView.onPause()
            renderer.onPause()
        }

        lifecycle.doOnDestroy {
            arCoreSessionHelper.onDestroy()
        }
    }

    private fun configureSession(session: Session) {
        session.configure(
            session.config.apply {
                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

                // Depth API is used if it is configured in Hello AR's settings.
                depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        Config.DepthMode.AUTOMATIC
                    } else {
                        Config.DepthMode.DISABLED
                    }

                // Instant Placement is used if it is configured in Hello AR's settings.
                instantPlacementMode =
                    if (instantPlacementSettings.instantPlacementEnabled) {
                        InstantPlacementMode.LOCAL_Y_UP
                    } else {
                        InstantPlacementMode.DISABLED
                    }
            }
        )
    }
}