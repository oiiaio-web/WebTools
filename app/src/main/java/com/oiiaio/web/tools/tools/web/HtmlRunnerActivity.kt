package com.oiiaio.web.tools.tools.web

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.oiiaio.web.tools.ui.BaseActivity
import com.oiiaio.web.tools.ui.grad
import com.oiiaio.web.tools.ui.gradStroke

class HtmlRunnerActivity : BaseActivity() {

    private val sample = """<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<style>
  body{font-family:system-ui;margin:0;padding:20px;background:linear-gradient(135deg,#4E54C8,#8E2DE2);color:#fff;text-align:center}
  h2{margin-bottom:14px}
  button{padding:12px 22px;font-size:16px;border:none;border-radius:12px;background:#fff;color:#4E54C8;font-weight:bold}
  .card{background:rgba(255,255,255,.15);border-radius:16px;padding:18px;margin-top:16px;font-size:20px}
</style>
</head>
<body>
  <h2>Hello, WebTools</h2>
  <button onclick="add()">点我计数</button>
  <div class="card" id="n">0</div>
<script>
  var c = 0;
  function add(){ c++; document.getElementById('n').innerText = c; }
</script>
</body>
</html>"""

    private lateinit var codeEt: EditText
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(cBg)
            fitsSystemWindows = true
        }

        val title = TextView(this).apply {
            text = "HTML / CSS / JS 代码"
            setTextColor(cSub)
            textSize = 12.5f
            setPadding(dp(14f), dp(12f), 0, dp(6f))
        }
        root.addView(title)

        codeEt = EditText(this).apply {
            setText(sample)
            setTextColor(cText)
            setHintTextColor(cSub)
            textSize = 13f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.TOP or Gravity.START
            setPadding(dp(12f), dp(10f), dp(12f), dp(10f))
            background = gradStroke(cSurface, cStroke, 10f)
            setHorizontallyScrolling(false)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            ).apply { marginStart = dp(10f); marginEnd = dp(10f); bottomMargin = dp(8f) }
        }
        root.addView(codeEt)

        fun mkBtn(text: String, filled: Boolean, onClick: () -> Unit): MaterialButton {
            return MaterialButton(this).apply {
                this.text = text
                cornerRadius = dp(10f)
                insetTop = 0; insetBottom = 0
                stateListAnimator = null
                textSize = 14f
                if (filled) {
                    backgroundTintList = android.content.res.ColorStateList.valueOf(cPrimary)
                    setTextColor(android.graphics.Color.WHITE)
                } else {
                    backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                    strokeColor = android.content.res.ColorStateList.valueOf(cPrimary)
                    strokeWidth = dp(1f)
                    setTextColor(cPrimary)
                }
                setOnClickListener { onClick() }
                layoutParams = LinearLayout.LayoutParams(0, dp(44f), 1f).apply { marginEnd = dp(8f) }
            }
        }

        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(10f), 0, dp(10f), dp(8f))
        }
        val run = mkBtn("▶  运行预览", true) { runCode() }
        val example = mkBtn("示例", false) { codeEt.setText(sample); runCode() }
        val clear = mkBtn("清空", false) { codeEt.setText("") }
        btnRow.addView(run); btnRow.addView(example); btnRow.addView(clear)
        root.addView(btnRow)

        val divider = View(this).apply {
            setBackgroundColor(cStroke)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(0.6f))
        }
        root.addView(divider)

        val previewTitle = TextView(this).apply {
            text = "运行结果（WebView 沙盒）"
            setTextColor(cSub)
            textSize = 12.5f
            setPadding(dp(14f), dp(10f), 0, dp(6f))
        }
        root.addView(previewTitle)

        val container = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            ).apply { marginStart = dp(10f); marginEnd = dp(10f); bottomMargin = dp(10f) }
            background = gradStroke(cSurface, cStroke, 10f)
        }
        root.addView(container)

        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        container.addView(webView)

        setContentView(root)
        runCode()
    }

    private fun runCode() {
        webView.loadDataWithBaseURL(null, codeEt.text.toString(), "text/html", "UTF-8", null)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            (webView.parent as? FrameLayout)?.removeView(webView)
            webView.destroy()
        }
        super.onDestroy()
    }
}
