package ru.mobileup.samples.features.otp.sms_retriever

import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.launch

@Composable
fun SmsRetriever(
    onSmsRetrieve: (SmsRetrievingResult) -> Unit
) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val updatedOnSmsRetrieve by rememberUpdatedState(onSmsRetrieve)

    LaunchedEffect(Unit) {
        SmsRetriever.getClient(context).startSmsUserConsent(null)
    }

    DisposableEffect(Unit) {
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        val broadcast = SmsRetrieveBroadcastReceiver()

        ContextCompat.registerReceiver(
            context,
            broadcast,
            intentFilter,
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED
        )

        val job = scope.launch {
            broadcast.otpCode.collect {
                updatedOnSmsRetrieve(it)
            }
        }

        onDispose {
            context.unregisterReceiver(broadcast)
            job.cancel()
        }
    }
}