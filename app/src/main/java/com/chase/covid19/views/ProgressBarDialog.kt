package com.chase.covid19.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import com.chase.covid19.R
import kotlinx.android.synthetic.main.progress_bar_dialog.*

class ProgressBarDialog(context: Context, hasCancel: Boolean) : Dialog(context) {
    val hasCancel = hasCancel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_bar_dialog)
        setCancelable(false)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (hasCancel) {
            btnCancel.visibility = View.VISIBLE
        } else {
            btnCancel.visibility = View.GONE
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onBackPressed() {
        // do nothing
    }
}
