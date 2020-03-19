package com.chase.covid19.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.chase.covid19.BuildConfig
import com.chase.covid19.R
import com.chase.covid19.application.ApplicationLoader.Companion.info
import com.chase.covid19.network.RetrofitFactory
import com.chase.covid19.utils.NetworkUtils
import com.chase.covid19.utils.Prefs
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_spash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class SplashActivity : BaseActivity(), CoroutineScope by CoroutineScope(Dispatchers.Main) {
    var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spash)

        if (NetworkUtils.isNetworkConnected(splashRooView, showError = true))
            getQuestions()

        btnRetry.setOnClickListener {
            if (NetworkUtils.isNetworkConnected(splashRooView, showError = true)) {
                tv_error.visibility = View.GONE
                btnRetry.visibility = View.GONE
                pbloading.visibility = View.VISIBLE
                getQuestions()
            }
        }
    }

    private fun getQuestions() {
        val service = RetrofitFactory.retrofitService
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.config()
            try {
                withContext(Dispatchers.Main) {
                    if (response?.questionsList != null) {
                        if (response.info != null)
                            info = response.info
                        if (!response.welcome.isNullOrEmpty())
                            Prefs.welcomeText = response.welcome.toString()
                        if (!response.userRegisterTitle.isNullOrEmpty())
                            Prefs.submitText = response.userRegisterTitle.toString()

                        val serverVersionCode = response.versionCode?.toInt() ?: 0
                        if (serverVersionCode > BuildConfig.VERSION_CODE) {
                            showUpdateDialog()
                        } else {
                            goToNextScreen()
                        }
                    } else {
                        tv_error.visibility = View.VISIBLE
                        btnRetry.visibility = View.VISIBLE
                        pbloading.visibility = View.GONE
                    }
                }
            } catch (e: HttpException) {
                Log.e("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
        }
    }

    private fun goToNextScreen() {
        Handler().postDelayed({
            if (!Prefs.isSignUp) {
                finish()
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
            } else {
                finish()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
            }
        }, 1000)
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.update_dialog)
        dialog = builder.create()
        dialog?.setCancelable(false)
        dialog?.show()

        val updateMaterialButton = dialog?.findViewById<MaterialButton>(R.id.updateMaterialButton)
        val laterMaterialButton = dialog?.findViewById<MaterialButton>(R.id.laterMaterialButton)

        val buttonsTypeFace = ResourcesCompat.getFont(this, R.font.iransanslight)
        updateMaterialButton?.typeface = buttonsTypeFace
        laterMaterialButton?.typeface = buttonsTypeFace

        updateMaterialButton?.setOnClickListener {
            goToCafeBazaar()
        }
        laterMaterialButton?.setOnClickListener {
            dialog?.dismiss()
            goToNextScreen()
        }
    }

    private fun goToCafeBazaar() {
        val packageName = "co.health.covid"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("bazaar://details?id=$packageName")
        intent.setPackage("com.farsitel.bazaar")
        startActivity(intent)
    }
}
