package ru.mobileup.samples.features.ar.presentation.placement

import android.opengl.GLSurfaceView

class FakeARPlacementComponent : ARPlacementComponent {
    override val glSurfaceView: GLSurfaceView = GLSurfaceView(null)
}