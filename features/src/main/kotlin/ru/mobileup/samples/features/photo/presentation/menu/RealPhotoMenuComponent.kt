package ru.mobileup.samples.features.photo.presentation.menu

import android.os.Build
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.permissions.PermissionService
import ru.mobileup.samples.core.utils.componentScope

class RealPhotoMenuComponent(
    componentContext: ComponentContext,
    private val permissionService: PermissionService,
    private val onOutput: (PhotoMenuComponent.Output) -> Unit
) : ComponentContext by componentContext, PhotoMenuComponent {

    private val permissionsRecording = buildList {
        add(android.Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    override fun onCameraClick() {
        componentScope.launch {
            val permissionsResult = permissionService.requestPermissions(
                permissionsRecording.toList()
            )
            if (permissionsResult.isAllGranted) {
                onOutput(PhotoMenuComponent.Output.CameraRequested)
            }
        }
    }
}