package ru.mobileup.samples.features.video.presentation.player

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.strResDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mobileup.samples.core.dialog.standard.DialogButton
import ru.mobileup.samples.core.dialog.standard.StandardDialogData
import ru.mobileup.samples.core.dialog.standard.standardDialogControl
import ru.mobileup.samples.core.message.data.MessageService
import ru.mobileup.samples.core.message.domain.Message
import ru.mobileup.samples.core.sharing.data.SharingService
import ru.mobileup.samples.core.utils.Resource
import ru.mobileup.samples.core.utils.ResourceFormatted
import ru.mobileup.samples.core.utils.componentScope
import ru.mobileup.samples.features.R
import ru.mobileup.samples.features.video.data.RELATIVE_STORAGE_PATH
import ru.mobileup.samples.features.video.data.VideoFileManager
import ru.mobileup.samples.features.video.data.VideoRepository
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.data.render.transformer.VideoRender
import ru.mobileup.samples.features.video.domain.PlayerConfig
import ru.mobileup.samples.features.video.domain.VideoTransform
import ru.mobileup.samples.features.video.domain.events.VideoPlayerEvent
import ru.mobileup.samples.features.video.domain.states.PlayerState
import ru.mobileup.samples.features.video.domain.states.RenderState
import ru.mobileup.samples.features.video.presentation.player.controller.VideoPlayerController
import ru.mobileup.samples.features.video.presentation.player.preview.PlayerOrientation

class RealVideoPlayerComponent(
    private val uri: Uri,
    componentContext: ComponentContext,
    override val videoPlayerController: VideoPlayerController,
    private val videoRepository: VideoRepository,
    private val videoRender: VideoRender,
    private val videoFileManager: VideoFileManager,
    private val sharingService: SharingService,
    private val messageService: MessageService
) : ComponentContext by componentContext, VideoPlayerComponent {

    override val isPlaying = MutableStateFlow(false)

    override val playerConfig = MutableStateFlow(PlayerConfig.Off)

    override val playerState = MutableStateFlow(
        videoRepository.getVideoDurationMsByUri(uri).let { durationMs ->
            PlayerState.build(
                durationMs = durationMs
            )
        }
    )

    override val saveDialog = standardDialogControl("saveDialog")

    override val resetTransformDialog = standardDialogControl("resetTransformDialog")

    private val backCallback = BackCallback {
        playerState.update { it.copy(orientation = PlayerOrientation.Portrait) }
    }

    init {
        doOnCreate {
            videoPlayerController.setVideoPlayerEvent { playerEvent, _ ->
                when (playerEvent) {
                    is VideoPlayerEvent.PlayingStateChanged -> {
                        isPlaying.update { playerEvent.isPlaying }
                    }
                }
            }
            loadMediaFromUri()
            videoPlayerController.play()
        }

        doOnDestroy {
            videoPlayerController.setVideoPlayerEvent(null)
            videoPlayerController.release()
        }

        backHandler.register(backCallback)

        playerState
            .onEach {
                backCallback.isEnabled = it.orientation is PlayerOrientation.Landscape
            }
            .launchIn(componentScope)
    }

    override fun onUpdateConfig(playerConfig: PlayerConfig) {
        this.playerConfig.update {
            playerConfig
        }
    }

    override fun onUpdateVolume(volume: Float) {
        playerState.update { it.copy(volume = volume) }
        videoPlayerController.setVolume(playerState.value.volume)
    }

    override fun onUpdateSpeed(speed: Float) {
        playerState.update { it.copy(speed = speed) }
        videoPlayerController.setSpeed(playerState.value.speed)
    }

    override fun onUpdateVideoTransform(videoTransform: VideoTransform) {
        playerState.update {
            it.copy(videoTransform = videoTransform)
        }
    }

    override fun onResetVideoTransform() {
        resetTransformDialog.show(
            StandardDialogData(
                title = R.string.player_reset_transform_title.strResDesc(),
                message = R.string.player_reset_transform_message.strResDesc(),
                confirmButton = DialogButton(
                    text = R.string.reset_btn.strResDesc(),
                    action = {
                        onUpdateVideoTransform(VideoTransform.defaultValue)
                    }
                ),
                dismissButton = DialogButton(
                    text = R.string.video_dismiss_btn.strResDesc(),
                    action = resetTransformDialog::dismiss
                )
            )
        )
    }

    override fun onCut(startPositionMs: Long, endPositionMs: Long) {
        playerState.update {
            it.copy(startPositionMs = startPositionMs, endPositionMs = endPositionMs)
        }
        loadMediaFromUri()
    }

    private fun loadMediaFromUri() {
        videoPlayerController.setMedia(
            uri = uri,
            startPositionMs = playerState.value.startPositionMs,
            endPositionMs = playerState.value.endPositionMs
        )
    }

    override fun onUpdateFilter(glFilter: GlFilter) {
        playerState.update {
            it.copy(glFilter = glFilter)
        }
    }

    override fun onChangeOrientation() {
        playerState.update {
            it.copy(
                orientation = when (it.orientation) {
                    PlayerOrientation.Landscape -> PlayerOrientation.Portrait
                    PlayerOrientation.Portrait -> PlayerOrientation.Landscape
                }
            )
        }
    }

    override fun onSaveClick() {
        saveDialog.show(
            StandardDialogData(
                title = R.string.save_video_dialog_title.strResDesc(),
                message = R.string.save_video_dialog_message.strResDesc(),
                confirmButton = DialogButton(
                    text = R.string.video_confirm_btn.strResDesc(),
                    action = ::startRender
                ),
                dismissButton = DialogButton(
                    text = R.string.video_dismiss_btn.strResDesc(),
                    action = saveDialog::dismiss
                )
            )
        )
    }

    override fun onShareClick() {
        sharingService.shareMedia(
            uri = uri,
            mimeType = "video/*"
        )
    }

    override fun onCancelRender() {
        videoRender.cancel()
    }

    private fun startRender() {
        componentScope.launch {
            val state = playerState.value
            videoRender.execute(
                uri = uri,
                startPositionMs = state.startPositionMs,
                endPositionMs = state.endPositionMs,
                volume = state.volume,
                speed = state.speed,
                size = videoRepository.getVideoSizeByUri(uri),
                videoTransform = state.videoTransform,
                glFilter = state.glFilter
            ).collect { renderState ->
                playerState.update {
                    it.copy(
                        renderProgress = if (renderState is RenderState.InProgress) {
                            renderState.value
                        } else {
                            null
                        }
                    )
                }

                when (renderState) {
                    is RenderState.Error -> {
                        messageService.showMessage(
                            Message(
                                text = StringDesc.Resource(
                                    R.string.render_error
                                )
                            )
                        )
                    }

                    is RenderState.WasCanceled -> {
                        messageService.showMessage(
                            Message(
                                text = StringDesc.Resource(
                                    R.string.render_canceled
                                )
                            )
                        )
                    }

                    is RenderState.Success -> {
                        videoFileManager.moveVideoToMediaStore(
                            renderState.uri
                        )
                        messageService.showMessage(
                            Message(
                                text = StringDesc.ResourceFormatted(
                                    R.string.render_success, RELATIVE_STORAGE_PATH
                                )
                            )
                        )
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }
    }
}