package com.chase.covid19.application

import AppModule
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.chase.covid19.model.CovidInfo
import com.chase.covid19.utils.Prefs
import com.chibatching.kotpref.Kotpref
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

val domain = "ac19.ir"
val baseUrl = "https://$domain"

class ApplicationLoader : Application() {
    companion object {
        var info = CovidInfo("", arrayListOf())
        var urls = ArrayList<String>()

        @Volatile
        var firebaseAnalytics: FirebaseAnalytics? = null
        lateinit var app: ApplicationLoader
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        app = this
        Kotpref.init(applicationContext)

        (1..10).map { "https://api$it.ac19.ir/v1/" }.toCollection(urls)

        initFcm()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        startKoin {
            androidContext(this@ApplicationLoader)
            // declare modules
            // Unused modules are deleted
            modules(AppModule)
        }
    }

    private fun initFcm() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Firebase", "getInstanceId failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result?.token
                Log.d("Firebase", "fcm token $token")
            }

        Log.d("Firebase", "fcm token " + Prefs.FCMToken)
    }
}