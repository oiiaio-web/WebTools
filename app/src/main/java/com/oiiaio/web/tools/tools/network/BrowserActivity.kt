package com.oiiaio.web.tools.tools.network

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.oiiaio.web.tools.R
import com.oiiaio.web.tools.ui.BaseActivity
import com.oiiaio.web.tools.ui.grad
import com.oiiaio.web.tools.ui.gradStroke
import java.net.URLEncoder

class BrowserActivity : BaseActivity() {

    private lateinit var webView: WebView
    private lateinit var etUrl: EditText
    private lateinit var progress: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        etUrl = findViewById(R.id.et_url)
        etUrl.background = gradStroke(cSurface, cStroke, 10f)

        findViewById<TextView>(R.id.btn_go).apply {
            background = grad(cPrimary, radiusDp = 10f)
            setOnClickListener { navigate() }
        }
        progress = findViewById(R.id.progress)

        val container = findViewById<FrameLayout>(R.id.web_container)
        webView = WebView(this)
        container.addView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
            override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                etUrl.setText(url)
                progress.visibility = android.view.View.VISIBLE
            }
            override fun onPageFinished(view: WebView, url: String?) {
                progress.visibility = android.view.View.GONE
                title = view.title
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) android.view.View.GONE
                else android.view.View.VISIBLE
            }
        }
        webView.setDownloadListener { url, _, _, _, _ ->
            runCatching {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }

        findViewById<TextView>(R.id.btn_back).setOnClickListener {
            if (webView.canGoBack()) webView.goBack()
        }
        findViewById<TextView>(R.id.btn_forward).setOnClickListener {
            if (webView.canGoForward()) webView.goForward()
        }
        findViewById<TextView>(R.id.btn_refresh).setOnClickListener { webView.reload() }

        etUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                navigate()
                true
            } else false
        }

        webView.loadUrl("https://www.bing.com")
    }

    private fun navigate() {
        val q = etUrl.text.toString().trim()
        if (q.isEmpty()) return
        val url = when {
            q.startsWith("http://") || q.startsWith("https://") -> q
            q.contains(" ") || !q.contains(".") ->
                "https://www.bing.com/search?q=" + URLEncoder.encode(q, "UTF-8")
            else -> "https://$q"
        }
        webView.loadUrl(url)
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(etUrl.windowToken, 0)
        webView.requestFocus()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    override fun onPause() {
        if (::webView.isInitialized) webView.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (::webView.isInitialized) webView.onResume()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            (webView.parent as? FrameLayout)?.removeView(webView)
            webView.destroy()
        }
        super.onDestroy()
    }
}
