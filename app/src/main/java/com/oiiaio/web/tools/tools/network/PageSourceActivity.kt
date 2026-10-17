package com.oiiaio.web.tools.tools.network

import com.oiiaio.web.tools.ui.BaseToolActivity
import java.net.HttpURLConnection
import java.net.URL

class PageSourceActivity : BaseToolActivity() {
    override fun toolTitle() = "网页源码查看"

    private data class Page(
        val finalUrl: String,
        val code: Int,
        val contentType: String,
        val charset: String,
        val sizeKb: Double,
        val html: String
    )

    private fun fetch(rawUrl: String): Page {
        val urlStr = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://"))
            rawUrl else "https://$rawUrl"
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 15000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/120 Mobile Safari/537.36")
            setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*")
        }
        val code = conn.responseCode
        val contentType = conn.contentType ?: "unknown"
        val bytes = (if (code in 200..399) conn.inputStream else conn.errorStream).use { it.readBytes() }
        var charset = "UTF-8"
        Regex("charset=([\\w-]+)", RegexOption.IGNORE_CASE).find(contentType)?.let {
            charset = it.groupValues[1]
        }
        if (!contentType.contains("charset", true) && bytes.isNotEmpty()) {
            val head = String(bytes, 0, minOf(bytes.size, 4096), Charsets.ISO_8859_1)
            Regex("charset=[\"']?([\\w-]+)", RegexOption.IGNORE_CASE).find(head)?.let {
                charset = it.groupValues[1].replace("utf8", "UTF-8", true)
            }
        }
        val html = String(bytes, charset(charset))
        return Page(
            conn.url.toString(), code, contentType, charset,
            bytes.size / 1024.0, html
        )
    }

    override fun onBuild() {
        val out = output("网页 HTML 源码", minLines = 12)

        label("目标网址", 4)
        card {
            val url = input("https://example.com", mono = true)
            addView(url)
            lateinit var btn: com.google.android.material.button.MaterialButton
            btn = primaryBtn("获取源码") {
                if (url.text.isBlank()) {
                    toast("请输入网址")
                    return@primaryBtn
                }
                btn.isEnabled = false
                btn.text = "获取中…"
                Thread {
                    try {
                        val p = fetch(url.text.toString().trim())
                        runOnUiThread {
                            out.setText(buildString {
                                append("网址：${p.finalUrl}\n")
                                append("状态：${p.code}\n")
                                append("类型：${p.contentType}\n")
                                append("编码：${p.charset}\n")
                                append("大小：%.1f KB\n".format(p.sizeKb))
                                append("────────────────────\n")
                                append(p.html)
                            })
                        }
                    } catch (e: Exception) {
                        runOnUiThread { out.setText("✗ 获取失败：${e.javaClass.simpleName}\n${e.message}") }
                    } finally {
                        runOnUiThread { btn.isEnabled = true; btn.text = "获取源码" }
                    }
                }.start()
            }
            addView(row(btn))
        }

        label("源码")
        addView(out)
        addView(resultActions(out))
    }
}
