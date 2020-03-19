package com.chase.covid19.reciever

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Pattern

class SMSReceiver : BroadcastReceiver() {
    private var otpListener: OTPReceiveListener? = null

    /**
     * @param otpListener
     */
    fun setOTPListener(otpListener: OTPReceiveListener?) {
        this.otpListener = otpListener
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null)
            return

        var message = ""
        val hash = "aaaaaaaa"
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val bundle = intent.extras
            if (bundle != null)
                message = bundle[SmsRetriever.EXTRA_SMS_MESSAGE] as String
        } else {
            val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (msgs.isNullOrEmpty())
                return

            for (i in msgs.indices) {
                message += msgs[i].messageBody
            }
            if (!message.contains(hash)) {
                return
            }
        }

        if (TextUtils.isEmpty(message))
            return

        val pattern = Pattern.compile("[0-9\\-]+")
        val matcher = pattern.matcher(message)

        if (!message.contains(hash)) {
            return
        }
        if (matcher.find()) {
            val code = matcher.group(0).replace("-", "")
            if (code.length >= 5) {

                otpListener?.onOTPReceived(code)
            }
        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
        fun onOTPReceivedError(error: String?)
    }
}