// Is it possible to use ir.ac19 package name.
package com.chase.covid19.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chase.covid19.model.NetworkError
import com.chase.covid19.network.OnApiGeneralEventListener
import com.chase.covid19.network.startListenApi
import com.chase.covid19.network.stopListenApi

abstract class BaseActivity : AppCompatActivity(), OnApiGeneralEventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startListenApi()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopListenApi()
    }

    override fun onInvalidCode(e: NetworkError) {
        finish()
    }
}