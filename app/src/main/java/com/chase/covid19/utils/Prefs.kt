package com.chase.covid19.utils

import com.chibatching.kotpref.KotprefModel

object Prefs : KotprefModel() {
    var mobile by stringPref()
    var tempMobileForResend by stringPref()
    var gender by stringPref()
    var birthDate by stringPref()
    var verificataionCode by stringPref()
    var userLocation by stringPref()
    var isSignUp by booleanPref(false)
    var isDataSend by booleanPref(false)
    var FCMToken by stringPref()
    var lastWebPage by stringPref()
    var isTutorialForAddButtonShown by booleanPref(false)
    var isTutorialForAddButtonShownSecondTime by booleanPref(false)
    var welcomeText by stringPref()
    var submitText by stringPref()
}