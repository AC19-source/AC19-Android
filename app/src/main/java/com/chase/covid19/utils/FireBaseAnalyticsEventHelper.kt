package com.chase.covid19.utils

import android.os.Bundle
import com.chase.covid19.application.ApplicationLoader

object FireBaseAnalyticsEventHelper {
    fun signUpEvent(userPhone: String) {
        try {
            val bundle = Bundle()
            bundle.putString("userPhone", userPhone)
            ApplicationLoader.firebaseAnalytics?.logEvent("SignUp", bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
