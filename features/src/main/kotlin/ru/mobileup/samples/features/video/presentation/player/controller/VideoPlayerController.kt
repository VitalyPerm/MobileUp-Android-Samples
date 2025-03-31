package ru.mobileup.samples.features.video.presentation.player.controller

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mobileup.samples.features.video.domain.events.VideoPlayerEvent

private const val POSITION_REFRESH_DELAY_MILLIS = 18L

@UnstableApi
class VideoPlayerController(
    private val context: Context,
    private val onVideoPlayerEvent: (VideoPlayerEvent, VideoPlayerController) -> Unit,
) : Player.Listener {
    private val progressScope = CoroutineScope(Dispatchers.Main)
    private val factory: DefaultMediaSourceFactory = DefaultMediaSourceFactory(context)
    private val _progressState = MutableStateFlow(0f)

    private var isBuffering = false
    private var progressJob: Job? = null

    val progressState = _progressState.asStateFlow()

    var player: ExoPlayer = createExoPlayer()
        private set

    override fun onPlaybackStateChanged(playbackState: Int) {
        // Don't listen position during buffering
        isBuffering = playbackState == Player.STATE_BUFFERING
        super.onPlaybackStateChanged(playbackState)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        onVideoPlayerEvent(
            VideoPlayerEvent.PlayingStateChanged(
                isPlaying = isPlaying,
                playFrom = 0f
            ), this@VideoPlayerController
        )
        if (isPlaying) {
            startVideoProgressUpdate()
        }
    }

    fun setMedia(
        uri: Uri,
        startPositionMs: Long,
        endPositionMs: Long
    ) {
        player.configure(
            uri = uri,
            startPositionMs = startPositionMs,
            endPositionMs = endPositionMs
        )
    }

    fun setVolume(
        volume: Float
    ) {
        player.volume = volume
    }

    fun setSpeed(
        speed: Float
    ) {
        player.setPlaybackSpeed(speed)
    }

    fun setProgress(progress: Float) {
        _progressState.tryEmit(progress)
        player.seekTo((player.duration * progress).toLong())
    }

    fun play() {
        if (player.currentPosition >= player.duration) {
            player.seekTo(0)
        }
        player.play()
    }

    fun pause() {
        player.run {
            pause()
            cancelVideoProgressUpdate()
        }
    }

    fun release() {
        player.release()
        cancelVideoProgressUpdate()
    }

    private fun createExoPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context, factory)
            .setSeekParameters(SeekParameters.EXACT)
            .build()
            .apply {
                playWhenReady = false
                addListener(this@VideoPlayerController)
                prepare()
            }
    }

    private fun ExoPlayer.configure(
        uri: Uri,
        startPositionMs: Long,
        endPositionMs: Long
    ) {
        repeatMode = Player.REPEAT_MODE_OFF
        this.playWhenReady = playWhenReady
        clearMediaItems()

        val mediaSource = ClippingMediaSource(
            factory.createMediaSource(MediaItem.fromUri(uri)),
            startPositionMs * 1_000,
            endPositionMs * 1_000,
            false,
            false,
            true
        )

        setMediaSource(mediaSource)
        videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        prepare()
    }

    private fun startVideoProgressUpdate() {
        cancelVideoProgressUpdate()
        progressJob = progressScope.launch {
            videoProgressUpdate()
        }
    }

    private fun cancelVideoProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    private suspend fun videoProgressUpdate() {
        player.run {
            if (!isBuffering && isPlaying) {
                delay(POSITION_REFRESH_DELAY_MILLIS)
                _progressState.emit(player.currentPosition / duration.toFloat())
                videoProgressUpdate()
            }
        }
    }
}