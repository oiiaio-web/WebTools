package com.oiiaio.web.tools.tools.network

import com.google.android.material.button.MaterialButton
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.net.HttpURLConnection
import java.net.URL

class HttpClientActivity : BaseToolActivity() {
    override fun toolTitle() = "HTTP 请求调试"

    private data class HttpResponse(
        val code: Int,
        val message: String,
        val elapsed: Long,
        val headers: String,
        val body: String
    )

    private val methods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")
    private var method = "GET"

    private fun doRequest(method: String, rawUrl: String, rawHeaders: String, body: String): HttpResponse {
        val urlStr = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://"))
            rawUrl else "https://$rawUrl"
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 15000
            instanceFollowRedirects = true
            requestMethod = method
            setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14) WebTools")
            setRequestProperty("Accept", "*/*")
        }
        rawHeaders.lines().filter { it.contains(":") }.forEach { line ->
            val idx = line.indexOf(":")
            conn.setRequestProperty(line.substring(0, idx).trim(), line.substring(idx + 1).trim())
        }
        val withBody = method in listOf("POST", "PUT", "PATCH", "DELETE") && body.isNotEmpty()
        if (withBody) {
            conn.doOutput = true
            if (conn.getRequestProperty("Content-Type") == null) {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        }
        val start = System.currentTimeMillis()
        val code = conn.responseCode
        val msg = conn.responseMessage ?: ""
        val elapsed = System.currentTimeMillis() - start
        val headers = conn.headerFields
            ?.filterKeys { it != null }
            ?.entries?.joinToString("\n") { (k, v) -> "$k: ${v.joinToString(", ")}" }
            ?: ""
        val stream = if (code in 200..399) conn.inputStream else conn.errorStream
        val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
        conn.disconnect()
        return HttpResponse(code, msg, elapsed, headers, text)
    }

    override fun onBuild() {
        val out = output("响应内容（状态 / 响应头 / 响应体）", minLines = 10)

        label("请求设置", 4)
        card {
            addView(dropdown(methods, 0) { _, m -> method = m })
            val url = input("请求地址，例如 https://httpbin.org/get", mono = true)
            addView(url)
            addView(labelText("请求头（每行一个，格式 Key: Value，可空）"))
            val headers = input("Accept: application/json", multi = true, mono = true, minLines = 2)
            addView(headers)
            addView(labelText("请求体（POST / PUT / PATCH 使用，可空）"))
            val body = input("{\"key\":\"value\"}", multi = true, mono = true, minLines = 4)
            addView(body)
            val send = primaryBtn("发送请求") { }
            addView(row(send))
            send.setOnClickListener {
                if (url.text.isBlank()) {
                    toast("请输入请求地址")
                    return@setOnClickListener
                }
                send.isEnabled = false
                send.text = "请求中…"
                Thread {
                    try {
                        val r = doRequest(
                            method,
                            url.text.toString().trim(),
                            headers.text.toString(),
                            body.text.toString()
                        )
                        runOnUiThread {
                            out.setText(buildString {
                                append("===== 状态 =====\n")
                                append("${r.code} ${r.message}    耗时 ${r.elapsed} ms\n\n")
                                append("===== 响应头 =====\n")
                                append(r.headers.ifEmpty { "(无)" })
                                append("\n\n===== 响应体 =====\n")
                                append(r.body.ifEmpty { "(无响应体)" })
                            })
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            out.setText("✗ 请求失败：${e.javaClass.simpleName}\n${e.message}")
                        }
                    } finally {
                        runOnUiThread {
                            send.isEnabled = true
                            send.text = "发送请求"
                        }
                    }
                }.start()
            }
        }

        label("响应结果")
        addView(out)
        addView(resultActions(out))
    }
}
