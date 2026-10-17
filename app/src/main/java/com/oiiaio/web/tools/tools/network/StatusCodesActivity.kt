package com.oiiaio.web.tools.tools.network

import android.graphics.Typeface
import android.widget.TextView
import com.oiiaio.web.tools.ui.BaseToolActivity

class StatusCodesActivity : BaseToolActivity() {
    override fun toolTitle() = "HTTP 状态码查询"

    private data class Code(val code: Int, val text: String)

    private val groups = listOf(
        "1xx 信息响应" to listOf(
            Code(100, "Continue 继续，请求已接收"),
            Code(101, "Switching Protocols 切换协议"),
            Code(103, "Early Hints 提前提示")
        ),
        "2xx 成功响应" to listOf(
            Code(200, "OK 请求成功"),
            Code(201, "Created 已创建"),
            Code(202, "Accepted 已接受"),
            Code(204, "No Content 无内容"),
            Code(206, "Partial Content 部分内容")
        ),
        "3xx 重定向" to listOf(
            Code(301, "Moved Permanently 永久重定向"),
            Code(302, "Found 临时重定向"),
            Code(303, "See Other 查看其它地址"),
            Code(304, "Not Modified 资源未修改"),
            Code(307, "Temporary Redirect 临时重定向（保持方法）"),
            Code(308, "Permanent Redirect 永久重定向（保持方法）")
        ),
        "4xx 客户端错误" to listOf(
            Code(400, "Bad Request 请求参数错误"),
            Code(401, "Unauthorized 未认证"),
            Code(403, "Forbidden 拒绝访问"),
            Code(404, "Not Found 资源不存在"),
            Code(405, "Method Not Allowed 请求方法不允许"),
            Code(408, "Request Timeout 请求超时"),
            Code(409, "Conflict 资源冲突"),
            Code(410, "Gone 资源已删除"),
            Code(413, "Payload Too Large 请求体过大"),
            Code(414, "URI Too Long 请求 URI 过长"),
            Code(429, "Too Many Requests 请求过于频繁（限流）")
        ),
        "5xx 服务器错误" to listOf(
            Code(500, "Internal Server Error 服务器内部错误"),
            Code(501, "Not Implemented 未实现"),
            Code(502, "Bad Gateway 网关错误"),
            Code(503, "Service Unavailable 服务不可用"),
            Code(504, "Gateway Timeout 网关超时"),
            Code(505, "HTTP Version Not Supported 不支持的 HTTP 版本")
        )
    )

    private fun colorFor(code: Int): Int = when (code) {
        in 100..199 -> 0xFF5C8CE8.toInt()
        in 200..299 -> 0xFF36B6A8.toInt()
        in 300..399 -> 0xFFE0A03C.toInt()
        in 400..499 -> 0xFFE0667A.toInt()
        else -> 0xFF9D5CFF.toInt()
    }

    override fun onBuild() {
        groups.forEach { (title, codes) ->
            label(title, 4)
            card {
                codes.forEach { c ->
                    val row = LinearLayoutRow(c)
                    addView(row)
                }
            }
        }
    }

    private fun LinearLayoutRow(c: Code): android.widget.LinearLayout {
        return android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, dp(9f), 0, dp(9f))
            isClickable = true
            setOnClickListener { copy(c.code.toString(), "状态码") }
            addView(TextView(this@StatusCodesActivity).apply {
                text = c.code.toString()
                setTextColor(colorFor(c.code))
                textSize = 14f
                typeface = Typeface.MONOSPACE
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    dp(52f), android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
            addView(TextView(this@StatusCodesActivity).apply {
                text = c.text
                setTextColor(cText)
                textSize = 13f
            })
        }
    }
}
