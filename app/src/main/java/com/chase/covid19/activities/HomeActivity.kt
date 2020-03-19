package com.chase.covid19.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.chase.covid19.BuildConfig
import com.chase.covid19.R
import com.chase.covid19.application.ApplicationLoader
import com.chase.covid19.application.baseUrl
import com.chase.covid19.application.domain
import com.chase.covid19.model.LaunchModel
import com.chase.covid19.network.apiCall
import com.chase.covid19.utils.Extra
import com.chase.covid19.utils.Prefs
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.statistics_view_header.*
import kotlinx.android.synthetic.main.statistics_view_row.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : BaseActivity()  , CoroutineScope by CoroutineScope(Dispatchers.Main){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        callLaunchApi()
        setClickListeners()
        fillStatisticsUI()
    }

    private fun fillStatisticsUI() {
        try {
            val statItems = ApplicationLoader.info.rows
            if(!statItems.isNullOrEmpty()){
                statistics_layout.visibility = View.VISIBLE

                rows.removeAllViews()
                statItems.forEach { stat ->
                    val row = LayoutInflater.from(this).inflate(R.layout.statistics_view_row, rows, false)
                    row.title.text = stat.ttl
                    row.death.text = stat.deaths.toString()
                    row.total.text = stat.total.toString()
                    row.recovered.text = stat.rec.toString()
                    rows.addView(row, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                }

            } else {
                statistics_layout.visibility = View.GONE
            }

            lastupdate.text = "بروزرسانی : "+ApplicationLoader.info.update
        }catch (e : Exception){
            // Please handle exception or remove this catch block.
        }
    }

    private fun setClickListeners() {
        ll_corona_test.setOnClickListener {
            startActivity(Intent(applicationContext ,TestListActivity::class.java ))
        }

        btn_logout.setOnClickListener {
            Prefs.clear()
            finish()
            startActivity(Intent(applicationContext , SignUpActivity::class.java))
        }

        newsCard.setOnClickListener {
            Prefs.lastWebPage = getString(R.string.news)
            startActivity(Intent(applicationContext, WebViewActivity::class.java).putExtra(Extra.url,
                "$baseUrl/news?mobile=true"
            ))
        }

        list_marakez.setOnClickListener {
            Prefs.lastWebPage = getString(R.string.medical_center)

            startActivity(Intent(applicationContext, WebViewActivity::class.java).putExtra(Extra.url,
                "$baseUrl/center?mobile=true"
            ))
        }

        notifications.setOnClickListener {
            Prefs.lastWebPage = getString(R.string.notices)

            startActivity(Intent(applicationContext, WebViewActivity::class.java).putExtra(Extra.url,
                "$baseUrl/alert?mobile=true"
            ))
        }

        call.setOnClickListener {
            Prefs.lastWebPage =  getString(R.string.educations)

            startActivity(Intent(applicationContext, WebViewActivity::class.java).putExtra(Extra.url,
                "$baseUrl/education?mobile=true"
            ))
        }

        popup.setOnClickListener {
            val popup = PopupMenu(this@HomeActivity, popup)
            popup.menuInflater.inflate(R.menu.main_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.exit -> logout()
                    R.id.about_us -> onAboutUsClicked()
                }
                true
            }

            popup.show() //showing popup menu
        }
    }

    private fun callLaunchApi() {
        try {
            val manufacturer: String = Build.MANUFACTURER
            val model: String = Build.MODEL

            val launchMode = LaunchModel(Prefs.FCMToken,
                                         "Android",
                                         BuildConfig.VERSION_NAME,
                                         manufacturer + model)
            launch { apiCall { launch("+98" + Prefs.mobile, Prefs.verificataionCode, launchMode) } }

        } catch (e: Exception) {
            // Please handle exception or remove this catch block.
        }
    }

    private fun logout() {
        Prefs.clear()
        finish()
        startActivity(Intent(applicationContext , SignUpActivity::class.java))
    }

    private fun onAboutUsClicked() {
        Prefs.lastWebPage = getString(R.string.about_us)
        val intent = Intent(applicationContext, WebViewActivity::class.java)
            .putExtra(Extra.url, "$baseUrl/aboutapp")
        startActivity(intent)
    }
}
