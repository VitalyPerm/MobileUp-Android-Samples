package ru.mobileup.samples.core.sharing.data

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.service.chooser.ChooserAction
import androidx.annotation.RequiresApi
import ru.mobileup.samples.core.R
import ru.mobileup.samples.core.error_handling.ExternalAppNotFoundException

class SharingServiceImpl(
    private val context: Context
) : SharingService {

    override fun shareMedia(uri: Uri, mimeType: String) = safeActivityLaunch {
        Intent.createChooser(
            Intent(Intent.ACTION_SEND)
                .setType(mimeType)
                .putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).apply {
                    clipData = ClipData.newRawUri("", uri)
                }, null
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                putExtra(Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, createCustomActions(uri, mimeType))
            }
        }
    }

    override fun shareText(text: String) = safeActivityLaunch {
        Intent.createChooser(
            Intent(Intent.ACTION_SEND)
                .setType("text/*")
                .putExtra(Intent.EXTRA_TEXT, text), null
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun createCustomActions(
        uri: Uri,
        mimeType: String
    ) = arrayOf(
        ChooserAction.Builder(
            Icon.createWithResource(context, R.drawable.ic_24_eye_on),
            context.getString(R.string.common_view),
            PendingIntent.getActivity(
                context,
                0,
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).build()
    )

    private fun safeActivityLaunch(intent: () -> Intent) {
        try {
            context.startActivity(intent(), null)
        } catch (e: ActivityNotFoundException) {
            throw ExternalAppNotFoundException(e)
        }
    }
}