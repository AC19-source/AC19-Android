package com.chase.covid19.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.util.Linkify
import android.view.Window
import android.widget.LinearLayout
import com.chase.covid19.R
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.result_dialog.*

class ProgressBarResultDialog(
    context: Context,
    val activity: Activity,
    val message: String,
    val color: String?) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.result_dialog)
        setCancelable(false)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window: Window = window
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                         LinearLayout.LayoutParams.MATCH_PARENT)

        tv_message.text = message
        Linkify.addLinks(tv_message, Linkify.WEB_URLS)

        if (!color.isNullOrEmpty()) {
            when(color) {
                "GREEN", "RED" -> FirebaseMessaging.getInstance().subscribeToTopic(color.toLowerCase())
                else -> FirebaseMessaging.getInstance().subscribeToTopic("red")
            }
        }

        val assetFileName = when (color) {
            "GREEN", "RED" -> "${color.toLowerCase()}.json"
            else -> "blue.json"
        }
        animation_view.setAnimation(assetFileName)

        btnCancel.setOnClickListener {
            dismiss()
            activity.finish()
        }
    }

    override fun onBackPressed() {
        // do nothing
    }
}
