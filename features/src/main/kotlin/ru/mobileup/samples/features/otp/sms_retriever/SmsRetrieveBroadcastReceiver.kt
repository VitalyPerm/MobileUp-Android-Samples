package ru.mobileup.samples.features.otp.sms_retriever

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.mobileup.samples.core.utils.getActivity
import ru.mobileup.samples.core.utils.getParcelableExt

class SmsRetrieveBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val BROADCAST_RECEIVER_KEY = "SmsRetrieveBroadcastReceiver"
    }

    val otpCode: MutableSharedFlow<SmsRetrievingResult> =
        MutableSharedFlow(0, 1, BufferOverflow.DROP_OLDEST)

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val smsRetrieverStatus: Status =
                extras?.getParcelableExt(SmsRetriever.EXTRA_STATUS)
                    ?: run { otpCode.tryEmit(SmsRetrievingResult.Error); return }

            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val activityResultRegistry =
                        (context.getActivity() as ComponentActivity).activityResultRegistry
                    startActivityForSmsConsent(activityResultRegistry, extras)
                }
                CommonStatusCodes.TIMEOUT -> {
                    otpCode.tryEmit(SmsRetrievingResult.Error)
                }
            }
        }
    }

    private fun startActivityForSmsConsent(
        activityResultRegistry: ActivityResultRegistry,
        extras: Bundle
    ) {
        try {
            val consentIntent: Intent = extras.getParcelableExt(SmsRetriever.EXTRA_CONSENT_INTENT)
                ?: run { otpCode.tryEmit(SmsRetrievingResult.Error); return }

            val getContent = activityResultRegistry.register(
                BROADCAST_RECEIVER_KEY,
                ActivityResultContracts.StartActivityForResult()
            ) { activityResult: ActivityResult? ->
                try {
                    if (activityResult?.resultCode == Activity.RESULT_OK && activityResult.data != null) {
                        val message = activityResult.data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                        val oneTimeCode = parseOneTimeCode(message!!)
                        otpCode.tryEmit(SmsRetrievingResult.Success(oneTimeCode))
                    } else {
                        otpCode.tryEmit(SmsRetrievingResult.Error)
                    }
                } catch (e: Exception) {
                    otpCode.tryEmit(SmsRetrievingResult.Error)
                }
            }
            getContent.launch(consentIntent)
        } catch (e: Exception) {
            otpCode.tryEmit(SmsRetrievingResult.Error)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun parseOneTimeCode(message: String): String {
        val digits = message.filter { it.isDigit() }
        require(digits.length in 4..8)
        return digits
    }
}
