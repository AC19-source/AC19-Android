package com.chase.covid19.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.chase.covid19.R
import com.google.android.material.snackbar.Snackbar

object NetworkUtils {
    fun isNetworkConnected(anchorView: View, showError: Boolean): Boolean {
        val ctx = anchorView.context
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo != null)
            return true

        if (showError) {
            val snackBar = Snackbar
                .make(anchorView,
                      ctx.getString(R.string.no_network_error),
                      Snackbar.LENGTH_SHORT
                ).setAction("Action", null)
            snackBar.show()
        }

        return false
    }
}