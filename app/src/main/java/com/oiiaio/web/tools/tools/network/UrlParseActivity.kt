package com.oiiaio.web.tools.tools.network

import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.net.URI
import java.net.URLDecoder

class UrlParseActivity : BaseToolActivity() {
    override fun toolTitle() = "URL 解析 / 拆分"

    private lateinit var host: LinearLayout
    private lateinit var params: LinearLayout

    override fun onBuild() {
        label("输入 URL")
        val inputEt = input("https://example.com/path?a=1&b=2#frag", mono = true)
        addView(inputEt)
        addView(row(primaryBtn("解析") {
            parse(inputEt.text.toString().trim())
        }))

        label("基本信息")
        host = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        card { addView(host) }
        label("查询参数")
        params = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        card { addView(params) }
    }

    private fun parse(raw: String) {
        host.removeAllViews()
        params.removeAllViews()
        try {
            val normalized = if (raw.startsWith("http")) raw else "https://$raw"
            val uri = URI(normalized)
            host.addView(infoRow("协议", uri.scheme ?: "-"))
            host.addView(infoRow("主机名", uri.host ?: "-"))
            host.addView(infoRow("端口", uri.port.takeIf { it != -1 }?.toString() ?: "默认"))
            host.addView(infoRow("路径", uri.rawPath ?: "/"))
            host.addView(infoRow("锚点", uri.fragment ?: "-"))

            val query = uri.rawQuery
            if (query.isNullOrBlank()) {
                params.addView(infoRow("查询参数", "(无)", false))
            } else {
                query.split("&").forEachIndexed { i, pair ->
                    val idx = pair.indexOf("=")
                    val k = if (idx >= 0) pair.substring(0, idx) else pair
                    val v = if (idx >= 0) pair.substring(idx + 1) else ""
                    params.addView(infoRow(
                        URLDecoder.decode(k, "UTF-8"),
                        runCatching { URLDecoder.decode(v, "UTF-8") }.getOrDefault(v),
                        true
                    ))
                }
            }
            toast("解析完成")
        } catch (e: Exception) {
            host.removeAllViews()
            host.addView(infoRow("错误", "URL 格式不正确：${e.message}", false))
            toast("解析失败")
        }
    }
}
