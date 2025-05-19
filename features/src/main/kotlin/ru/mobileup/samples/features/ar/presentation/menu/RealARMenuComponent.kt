package ru.mobileup.samples.features.ar.presentation.menu

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.permissions.PermissionService
import ru.mobileup.samples.core.utils.componentScope

class RealARMenuComponent(
    componentContext: ComponentContext,
    private val permissionService: PermissionService,
    private val onOutput: (ARMenuComponent.Output) -> Unit
) : ARMenuComponent, ComponentContext by componentContext {

    private val permissionCamera = listOf(android.Manifest.permission.CAMERA)

    override fun onPlacementClick() {
        componentScope.launch {
            val permissionResult = permissionService.requestPermissions(permissionCamera)
            if (permissionResult.isAllGranted) {
                onOutput(ARMenuComponent.Output.PlacementRequested)
            }
        }
    }
}