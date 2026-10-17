package com.oiiaio.web.tools.tools.network

import android.graphics.Typeface
import android.webkit.WebSettings
import android.widget.LinearLayout
import android.widget.TextView
import com.oiiaio.web.tools.ui.BaseToolActivity

class UserAgentActivity : BaseToolActivity() {
    override fun toolTitle() = "User-Agent 大全"

    private data class Ua(val name: String, val value: String)

    private val presets = listOf(
        Ua("Android · Chrome 120",
            "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"),
        Ua("iPhone · Safari (iOS 17)",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1"),
        Ua("iPad · Safari (iOS 17)",
            "Mozilla/5.0 (iPad; CPU OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1"),
        Ua("Windows · Chrome 120",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"),
        Ua("Windows · Edge 120",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0"),
        Ua("macOS · Safari 17",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15"),
        Ua("Linux · Firefox 121",
            "Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0"),
        Ua("微信内置浏览器（Android）",
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/116.0.0.0 Mobile Safari/537.36 MicroMessenger/8.0.44"),
        Ua("Googlebot 爬虫",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"),
        Ua("Baiduspider 爬虫",
            "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)")
    )

    private fun uaRow(name: String, ua: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            isClickable = true
            setOnClickListener { copy(ua, "UA") }
            addView(TextView(this@UserAgentActivity).apply {
                text = name
                setTextColor(cSub)
                textSize = 12.5f
            })
            addView(TextView(this@UserAgentActivity).apply {
                text = ua
                setTextColor(cText)
                textSize = 12f
                typeface = Typeface.MONOSPACE
                setPadding(0, dp(4f), 0, 0)
            })
        }
    }

    override fun onBuild() {
        label("本机 UA（点击复制）", 4)
        card {
            val me = WebSettings.getDefaultUserAgent(this@UserAgentActivity)
            addView(uaRow("本机 WebView 实际 UA", me))
        }

        label("常用预设（点击复制）")
        presets.forEach { u ->
            card { addView(uaRow(u.name, u.value)) }
        }
    }
}
