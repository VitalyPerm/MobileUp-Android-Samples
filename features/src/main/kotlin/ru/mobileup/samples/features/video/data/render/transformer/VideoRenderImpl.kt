package ru.mobileup.samples.features.video.data.render.transformer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Size
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.SpeedChangeEffect
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mobileup.samples.features.video.data.render.GlFilter
import ru.mobileup.samples.features.video.data.utils.VideoEditorDirectory
import ru.mobileup.samples.features.video.domain.VideoTransform
import ru.mobileup.samples.features.video.domain.states.RenderState
import java.io.File
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

private const val MP4_FORMAT = "mp4"

class VideoRenderImpl(
    private val context: Context
) : VideoRender {

    private var renderProcessJob: Job? = null
    private var currentTransformer: Transformer? = null

    override fun execute(
        uri: Uri,
        startPositionMs: Long,
        endPositionMs: Long,
        volume: Float,
        speed: Float,
        size: Size,
        videoTransform: VideoTransform,
        glFilter: GlFilter
    ): Flow<RenderState> {
        return channelFlow {
            renderProcessJob = launch {
                val renderDirectory = withContext(Dispatchers.IO) {
                    VideoEditorDirectory.Render
                        .toFile(context)
                        .also(File::mkdirs)
                }
                val outputFile = File(renderDirectory, "${UUID.randomUUID()}.$MP4_FORMAT")

                processRender(
                    outputFile = outputFile,
                    uri = uri,
                    startPositionMs = startPositionMs,
                    endPositionMs = endPositionMs,
                    volume = volume,
                    speed = speed,
                    size = size,
                    videoTransform = videoTransform,
                    glFilter = glFilter,
                    onProgress = {
                        trySend(RenderState.InProgress(it))
                    }
                ).apply {
                    join()
                }

                send(RenderState.Success(uri = outputFile.toUri()))
            }.apply {
                invokeOnCompletion { cause ->
                    if (cause is CancellationException) {
                        trySend(RenderState.WasCanceled)
                    } else if (cause != null) {
                        trySend(RenderState.Error(exception = cause))
                    }
                    close()
                }
            }
            awaitClose { this@VideoRenderImpl.cancel() }
        }
    }

    override fun cancel() {
        renderProcessJob?.cancel(CancellationException())
    }

    @OptIn(UnstableApi::class)
    private suspend fun processRender(
        outputFile: File,
        uri: Uri,
        startPositionMs: Long,
        endPositionMs: Long,
        volume: Float,
        speed: Float,
        size: Size,
        videoTransform: VideoTransform,
        glFilter: GlFilter,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.Main) {
        launch {
            val handler = Handler(Looper.getMainLooper())
            val duration = (endPositionMs - startPositionMs) / 1000f

            val mediaItemList = listOf(
                createSequence(
                    uri = uri,
                    startPositionMs = startPositionMs,
                    endPositionMs = endPositionMs,
                    volume = volume,
                    speed = speed,
                    size = size,
                    videoTransform = videoTransform,
                    glFilter = glFilter,
                    onProgressInSeconds = { progressInSeconds ->
                        handler.post {
                            onProgress(progressInSeconds / (duration / speed))
                        }
                    }
                )
            )

            val mediaSequenceList = mediaItemList.map {
                EditedMediaItemSequence(it)
            }.toList()

            val composition = Composition.Builder(mediaSequenceList).build()

            callbackFlow {
                currentTransformer = Transformer.Builder(context)
                    .setAudioMimeType(MimeTypes.AUDIO_AAC)
                    .setVideoMimeType(MimeTypes.VIDEO_H264)
                    .addListener(object : Transformer.Listener {
                        override fun onCompleted(
                            composition: Composition,
                            exportResult: ExportResult
                        ) {
                            super.onCompleted(composition, exportResult)
                            onProgress(1f)
                            trySend(Unit)
                            close()
                        }

                        override fun onError(
                            composition: Composition,
                            exportResult: ExportResult,
                            exportException: ExportException,
                        ) {
                            super.onError(composition, exportResult, exportException)
                            trySend(Unit)
                            close()
                        }
                    })
                    .build()

                currentTransformer?.start(composition, outputFile.path)

                awaitClose { close() }
            }.first()
        }.apply {
            invokeOnCompletion { cause ->
                if (cause is CancellationException) {
                    currentTransformer?.cancel()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun createSequence(
        uri: Uri,
        startPositionMs: Long,
        endPositionMs: Long,
        volume: Float,
        speed: Float,
        size: Size,
        videoTransform: VideoTransform,
        glFilter: GlFilter,
        onProgressInSeconds: (Float) -> Unit,
    ): EditedMediaItem {
        val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionUs(startPositionMs * 1_000)
            .setEndPositionUs(endPositionMs * 1_000)
            .setRelativeToDefaultPosition(false)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setClippingConfiguration(clippingConfiguration)
            .build()

        val videoEffects = mutableListOf<Effect>(
            SpeedChangeEffect(speed),
            FilterAndPositionGlEffect(
                inputSize = size,
                outputSize = size,
                glFilter = glFilter,
                videoTransform = videoTransform,
                onProgress = {
                    onProgressInSeconds(it)
                }
            ),
        )

        return EditedMediaItem.Builder(mediaItem)
            .setEffects(
                Effects(
                    listOf(
                        AudioVolumeEffect(volume),
                        SonicAudioProcessor().apply {
                            setSpeed(speed)
                        }
                    ),
                    videoEffects
                )
            )
            .build()
    }
}