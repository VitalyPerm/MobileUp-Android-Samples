package ru.mobileup.samples.features.otp.sms_retriever

sealed interface SmsRetrievingResult {
    class Success(val otpCode: String) : SmsRetrievingResult
    data object Error : SmsRetrievingResult
}