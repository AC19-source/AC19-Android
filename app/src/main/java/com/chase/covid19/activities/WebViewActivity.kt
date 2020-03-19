package com.chase.covid19.activities

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.webkit.WebSettings.LOAD_CACHE_ONLY
import android.webkit.WebSettings.LOAD_DEFAULT
import com.chase.covid19.R
import com.chase.covid19.utils.Extra
import com.chase.covid19.utils.Prefs
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : BaseActivity() {
    private fun isConnected(): Boolean {
        val cm = this.getSystemService(Activity.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return cm?.activeNetworkInfo?.isConnected == true
    }

    private fun autoSetCacheMode() {
        webView.settings.cacheMode = if (isConnected()) LOAD_DEFAULT else LOAD_CACHE_ONLY
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        back_button.setOnClickListener {
            onBackPressed()
        }

        tv_title.text = Prefs.lastWebPage

        webView.settings.javaScriptEnabled = true // enable javascript
        webView.settings.setAppCacheEnabled(false)
        webView.clearCache(true)
        autoSetCacheMode()

        webView.setWebViewClient(object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                view?.loadUrl("file:///android_asset/error.html")
                handleErr(view, description ?: "")
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView?,
                req: WebResourceRequest,
                rerr: WebResourceError
            ) {
                handleErr(view, rerr.description.toString())
            }

            var latestUrl: String = ""
            fun handleErr(webView: WebView?, err: String) {
                if (webView == null)
                    return

                if (webView.settings.cacheMode == LOAD_DEFAULT && !isConnected()) {
                    webView.settings.cacheMode = LOAD_CACHE_ONLY
                    webView.reload()
                } else {
                    autoSetCacheMode()
                    if (err != "net::ERR_CACHE_MISS") {
                        if (latestUrl != webView.url) {
                            latestUrl = webView.url
                            webView.reload()
                        }
                    }
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
                    ) { html ->
                        if (html.contains("Webpage not available")) {
                            webView.loadUrl("file:///android_asset/error.html")
                        }
                    }
                }
            }
        })

        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        webView.loadUrl(intent.getStringExtra(Extra.url))
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}