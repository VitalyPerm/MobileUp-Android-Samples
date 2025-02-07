package ru.mobileup.samples.features.video.presentation.menu

import android.os.Build
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.permissions.PermissionService
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.video.domain.VideoOption

class RealVideoMenuComponent(
    componentContext: ComponentContext,
    private val permissionService: PermissionService,
    private val onOutput: (VideoMenuComponent.Output) -> Unit
) : ComponentContext by componentContext, VideoMenuComponent {

    private val permissionsStorage =
        listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray()

    private val permissionsRecording = buildList {
        add(android.Manifest.permission.CAMERA)
        add(android.Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            addAll(permissionsStorage)
        }
    }.toTypedArray()

    override fun onVideoOptionChosen(videoOption: VideoOption) {
        componentScope.launch {
            when (videoOption) {
                is VideoOption.Recorder -> {
                    val permissionsResult = permissionService.requestPermissions(
                        permissionsRecording.toList()
                    )
                    if (permissionsResult.isAllGranted) {
                        onOutput(VideoMenuComponent.Output.VideoOptionChosen(videoOption))
                    }
                }

                is VideoOption.Player -> {
                    onOutput(VideoMenuComponent.Output.VideoOptionChosen(videoOption))
                }
            }
        }
    }
}