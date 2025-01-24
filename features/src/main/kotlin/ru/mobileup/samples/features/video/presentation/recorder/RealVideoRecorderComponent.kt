package ru.mobileup.samples.features.video.presentation.recorder

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.video.Quality
import com.arkivanov.decompose.ComponentContext
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.video.domain.states.RecorderState
import ru.mobileup.samples.features.video.domain.RecorderConfig

class RealVideoRecorderComponent(
    componentContext: ComponentContext,
    private val messageService: MessageService,
    private val onOutput: (VideoRecorderComponent.Output) -> Unit
) : ComponentContext by componentContext, VideoRecorderComponent {

    override val recorderConfig = MutableStateFlow(RecorderConfig.Off)

    override val recorderState = MutableStateFlow(RecorderState.build())

    override fun onUpdateConfig(recorderConfig: RecorderConfig) {
        this.recorderConfig.value = recorderConfig
    }

    override fun onUpdateIsRecording(isRecording: Boolean) {
        recorderState.value = recorderState.value.copy(
            isRecording = isRecording
        )
    }

    override fun onUpdateDuration(durationMs: Long) {
        recorderState.value = recorderState.value.copy(
            durationMs = durationMs
        )
    }

    override fun onRecordInitializationFailed() {
        messageService.showMessage(
            Message(
                text = StringDesc.Resource(
                    R.string.recorder_initialization_failed
                )
            )
        )
    }

    override fun onRecordCompleted(uri: Uri) {
        onOutput(VideoRecorderComponent.Output.RecordCompleted(uri))
    }

    override fun onRecordFailed() {
        messageService.showMessage(
            Message(
                text = StringDesc.Resource(
                    R.string.recorder_failed
                )
            )
        )
    }

    override fun onUpdateCameraSelector(cameraSelector: CameraSelector) {
        recorderState.value = recorderState.value.copy(
            cameraSelector = cameraSelector
        )
    }

    override fun onUpdateFps(fps: Int) {
        recorderState.value = recorderState.value.copy(
            fps = fps
        )
    }

    override fun onUpdateQuality(quality: Quality) {
        recorderState.value = recorderState.value.copy(
            quality = quality
        )
    }

    override fun onUpdateTorchState(torchState: Boolean) {
        recorderState.value = recorderState.value.copy(
            torchState = torchState
        )
    }
}