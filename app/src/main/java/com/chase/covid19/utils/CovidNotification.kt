package com.chase.covid19.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.getSystemService
import com.chase.covid19.BuildConfig
import com.chase.covid19.R
import com.chase.covid19.activities.SplashActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import inject
import ir.zadak.zadaknotify.interfaces.ImageLoader
import ir.zadak.zadaknotify.interfaces.OnImageLoadingCompleted
import ir.zadak.zadaknotify.notification.Load
import ir.zadak.zadaknotify.notification.ZadakNotification

class CovidNotification {
    val app by inject<Application>()

    companion object : ImageLoader {
        val app by inject<Application>()
        private lateinit var viewTarget: Target

        private fun getViewTarget(onCompleted: OnImageLoadingCompleted): Target {
            return object : Target {

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                }

                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    onCompleted.imageLoadingCompleted(bitmap)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                }
            }
        }

        override fun load(uri: String?, onCompleted: OnImageLoadingCompleted) {
            try {
                viewTarget = getViewTarget(onCompleted)
                Picasso.get().load(uri)
                    .placeholder(R.drawable.placeholderloading).into(viewTarget)
            } catch (ignore: Exception) {
            }
        }

        override fun load(imageResId: Int, onCompleted: OnImageLoadingCompleted) {
            try {
                viewTarget = getViewTarget(onCompleted)
                Picasso.get().load(imageResId).placeholder(R.drawable.placeholderloading)
                    .into(viewTarget)
            } catch (ignore: Exception) {
            }
        }

        var PUSH_TYPE_SIMPLE = 1 //added from version code 1
        var PUSH_TYPE_BIG_IMAGE = 2 //added from version code 1
        var PUSH_TYPE_BIG_TEXT = 3 //added from version code 1
        var PUSH_TYPE_POLL = 4
        var PUSH_TYPE_OPEN_URL = 5
        private const val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"

        private fun createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "My app notification channel"
                val description = "Description for this channel"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager =
                    getSystemService<NotificationManager>(app, NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context, recievedJson: String) {
            val notification: CovidNotification = jsonWrapper(recievedJson)

            createNotificationChannel()
            if (notification == null || notification.versionCode > BuildConfig.VERSION_CODE)
                return

            val notif = createNotification(context, notification)
            when (notification.pushType) {
                PUSH_TYPE_SIMPLE -> simpleNotification(notif, notification)
                PUSH_TYPE_BIG_IMAGE -> customImageNotification(notif, notification)
                PUSH_TYPE_BIG_TEXT -> bigTextNotification(notif, notification)
//            PUSH_TYPE_POLL -> deleteSpecifyNews(context, notification)
//            PUSH_TYPE_OPEN_URL ->
                else -> {
                }
            }
        }

        private fun createNotification(context: Context, notification: CovidNotification): Load {
            return ZadakNotification.with(context).load()
                .notificationChannelId(CHANNEL_ID)
                .smallIcon(R.drawable.ic_stat_name)
                .title(notification.title)
                .message(notification.message)
                .click(
                    if (notification.link.isNotEmpty())
                        openLink(notification.link, context)
                    else
                        getSplashIntent()
                )
                .largeIcon(R.drawable.ic_launcher)
        }

        private fun getSplashIntent(): PendingIntent {
            val intent = Intent(app, SplashActivity::class.java)
            return PendingIntent.getActivity(app, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun openLink(link: String, context: Context): PendingIntent {
            val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            return PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }

        private fun bigTextNotification(notif: Load, notification: CovidNotification) {
            if (notification.bigText.isNotEmpty()) {
                notif.autoCancel(true)
                    .flags(Notification.DEFAULT_ALL)
                    .bigTextStyle(notification.bigText, notification.message)
                    .simple()
                    .build()
            }
        }

        private fun customImageNotification(notif: Load, notification: CovidNotification) {
            Handler(Looper.getMainLooper()).post {
                notif.color(android.R.color.background_dark)
                    .custom()
                    .setImageLoader(this)
                    .background(notification.extraData)
                    .setPlaceholder(R.drawable.ic_placeholder)
                    .build()
            }
        }

        private fun simpleNotification(notif: Load, notification: CovidNotification) {
            notif.autoCancel(true)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build()
        }

        private fun jsonWrapper(json: String): CovidNotification {
            return try {
                val gson = Gson()
                gson.fromJson(json, CovidNotification::class.java)
            } catch (e: Exception) {
                // Log and remove e.printStackTrace()
                e.printStackTrace()
                return CovidNotification()
            }
        }
    }

    //region properties
    @SerializedName("id")
    private var id: Int = 0
    @SerializedName("title")
    private var title: String = ""
    @SerializedName("message")
    private var message: String = ""
    @SerializedName("bigText")
    private var bigText: String = ""
    @SerializedName("background")
    private var background: String = ""
    @SerializedName("pushType")
    private var pushType: Int = 0
    @SerializedName("link")
    private var link: String = ""
    @SerializedName("extraData")
    private var extraData: String = ""
    @SerializedName("extraDataType")
    private var extraDataType: Int = 0
    @SerializedName("versionCode")
    private var versionCode: Int = 0
    @SerializedName("pushNewsTagId")
    private var pushNewsTagId: Int = 0
    //endregion
}