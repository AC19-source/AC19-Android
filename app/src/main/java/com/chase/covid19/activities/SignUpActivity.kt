package com.chase.covid19.activities

import StringUtils
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.chase.covid19.R
import com.chase.covid19.network.apiCall
import com.chase.covid19.utils.FireBaseAnalyticsEventHelper
import com.chase.covid19.utils.NetworkUtils
import com.chase.covid19.utils.Prefs
import com.chase.covid19.views.ProgressBarDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import retrofit2.HttpException

class SignUpActivity : BaseActivity() {
    private lateinit var gregorianDate: String
    private var TILBirthDate: TextInputLayout? = null
    private var etBirthDate: TextInputEditText? = null
    private var progressDialog: ProgressBarDialog? = null
    private var phoneNumberValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_signup)
        progressDialog = ProgressBarDialog(this, true)

        if (Prefs.welcomeText.isNotEmpty()) {
            tv_message.text = Prefs.welcomeText
        }

        signup.setOnClickListener {
            if (NetworkUtils.isNetworkConnected(signup, showError = true))
                if (!checkErrors()) {
                    progressDialog!!.show()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = apiCall { register("+98" + etPhone.text.toString()) }
                            Prefs.tempMobileForResend = "+98" + etPhone.text.toString()
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    finish()
                                    startActivity(Intent(applicationContext,
                                                         VerifyActivity::class.java))

                                    val userPhone = etPhone.text.toString()
                                    Prefs.mobile = userPhone
                                    progressDialog!!.dismiss()

                                    FireBaseAnalyticsEventHelper.signUpEvent(userPhone)
                                } else {
                                    toast("Error network operation failed with ${response.code()}")
                                    progressDialog!!.dismiss()
                                }
                            }
                        } catch (e: HttpException) {
                            Log.e("REQUEST", "Exception ${e.message}")
                            progressDialog!!.dismiss()
                        } catch (e: Throwable) {
                            Log.e("REQUEST", "Ooops: Something else went wrong!")
                            progressDialog!!.dismiss()
                        }
                    }
                }
        }
    }

    private fun fillValidations() {
        phoneNumberValid = StringUtils.isValidPhone(etPhone.text.toString())
    }

    private fun checkErrors(): Boolean {
        var hasError = false

        fillValidations()

        if (!phoneNumberValid) {
            TILPhoneInputLayout.error = getString(R.string.wrong_phone)
            hasError = true
        } else {
            TILPhoneInputLayout.error = null
            TILPhoneInputLayout.isErrorEnabled = false
        }

        return hasError
    }
}