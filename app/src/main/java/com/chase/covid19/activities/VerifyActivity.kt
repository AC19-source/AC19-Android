package com.chase.covid19.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TimeUtils
import android.view.View
import com.chase.covid19.R
import com.chase.covid19.network.apiCall
import com.chase.covid19.reciever.SMSReceiver
import com.chase.covid19.utils.NetworkUtils
import com.chase.covid19.utils.Prefs
import com.chase.covid19.views.ProgressBarDialog
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_verify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class VerifyActivity : BaseActivity(), SMSReceiver.OTPReceiveListener {
    private var progressDialog: ProgressBarDialog? = null
    private var smsReceiver: SMSReceiver? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_verify)

        progressDialog = ProgressBarDialog(this, true)

        btn_verify.setOnClickListener {
            if (NetworkUtils.isNetworkConnected(verifyActivityRootView, showError = true)) {
                if (etCode.text?.toString()!!.isNotEmpty()) {
                    progressDialog!!.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response =
                                apiCall({ verify("+98" + Prefs.mobile, etCode.text.toString()) },
                                        true)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    finish()
                                    Prefs.isSignUp = true
                                    Prefs.verificataionCode = etCode.text.toString()
                                    progressDialog!!.dismiss()
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            HomeActivity::class.java
                                        )
                                    )
                                } else {
                                    toast("Error network operation failed with ${response.code()}")
                                    progressDialog!!.dismiss()
                                }
                            }
                        } catch (e: HttpException) {
                            Log.e("REQUEST", "Exception ${e.message}")
                            progressDialog!!.dismiss()
                        } catch (e: Throwable) {
                            Log.e("REQUEST", "Ooops: Something else went wrong")
                            progressDialog!!.dismiss()
                        }
                    }
                }
            }
        }

        securityBack.setOnClickListener {
            onBackPressed()
        }

        val minutes = 5
        val milliseconds = minutes * 60 * 1000

        countDownTimer = object : CountDownTimer(milliseconds.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
            }

            override fun onFinish() {
                send_again_des.text = getString(R.string.resend_code)
                send_again_des.textColor = R.color.blue
                timerText.visibility = View.INVISIBLE
            }
        }

        (countDownTimer as CountDownTimer).start()

        send_again_des.setOnClickListener {
            if (timerText.visibility == View.INVISIBLE) {
                send_again_des.text = getString(R.string.time_to_resend)
                timerText.visibility = View.VISIBLE
                (countDownTimer as CountDownTimer).start()
                if (NetworkUtils.isNetworkConnected(verifyActivityRootView, showError = true)) {
                    callResendApi()
                }
            }
        }

        startSMSListener()
    }

    private fun callResendApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiCall { register(Prefs.tempMobileForResend) }
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        toast(getString(R.string.code_sent))

                    } else {
                        toast("Error network operation failed with ${response.code()}")
                        progressDialog!!.dismiss()
                    }
                }
            } catch (e: HttpException) {
                Log.e("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onOTPReceived(otp: String?) {
        etCode.setText(otp)
        btn_verify.performClick()

        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
            smsReceiver = null
        }
    }

    override fun onOTPTimeOut() {
    }

    override fun onOTPReceivedError(error: String?) {
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver!!.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // API successfully started
            }
            task.addOnFailureListener {
                // Fail to start API
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (smsReceiver != null) {

            unregisterReceiver(smsReceiver)
            smsReceiver = null
        }
    }
}