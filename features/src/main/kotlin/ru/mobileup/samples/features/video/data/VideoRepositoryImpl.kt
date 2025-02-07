package ru.mobileup.samples.features.video.data

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
import android.net.Uri
import android.util.Size

class VideoRepositoryImpl(
    private val context: Context
) : VideoRepository {

    override fun getVideoDurationMsByUri(uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        return (retriever.extractMetadata(METADATA_KEY_DURATION))?.toLong() ?: 0L
    }

    override fun getVideoSizeByUri(uri: Uri): Size {
        val mediaMetadataRetriever = MediaMetadataRetriever()

        mediaMetadataRetriever.setDataSource(context, uri)

        val width = try {
            mediaMetadataRetriever.extractMetadata(METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
        } catch (ee: Exception) {
            0
        }

        val height = try {
            mediaMetadataRetriever.extractMetadata(METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
        } catch (ee: Exception) {
            0
        }

        val rotation = try {
            mediaMetadataRetriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0
        } catch (ee: Exception) {
            0
        }

        mediaMetadataRetriever.release()

        return if (rotation == 90 || rotation == 270) {
            Size(height, width)
        } else {
            Size(width, height)
        }
    }
}