package com.chase.covid19.sync

import com.chase.covid19.utils.CovidNotification
import com.chase.covid19.utils.Prefs
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CovidFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        FirebaseMessaging.getInstance().subscribeToTopic("ALL")
        Prefs.FCMToken = newToken
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val myCustomKey = data["customkey"]
        if (myCustomKey != null) {
            CovidNotification.showNotification(this, myCustomKey)
        }
    }
}