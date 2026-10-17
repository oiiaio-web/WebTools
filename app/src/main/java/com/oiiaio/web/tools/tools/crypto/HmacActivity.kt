package com.oiiaio.web.tools.tools.crypto

import com.oiiaio.web.tools.ui.BaseToolActivity
import com.oiiaio.web.tools.util.Crypto

class HmacActivity : BaseToolActivity() {
    override fun toolTitle() = "HMAC 消息认证码"

    override fun onBuild() {
        label("密钥 Key")
        val key = input("请输入 HMAC 密钥", mono = true)

        label("消息内容")
        val msg = input("要签名的消息", multi = true, mono = false, minLines = 4)

        var lower = false
        addView(switchRow("使用小写字母输出") { lower = it })

        val out = output("HMAC 结果", minLines = 5)
        val btn = primaryBtn("计算全部 HMAC") {
            val k = key.text.toString().toByteArray(Charsets.UTF_8)
            val data = msg.text.toString().toByteArray(Charsets.UTF_8)
            if (key.text.isEmpty()) {
                toast("请输入密钥")
                return@primaryBtn
            }
            val rows = listOf(
                "HmacMD5" to Crypto.hmac("HmacMD5", k, data),
                "HmacSHA1" to Crypto.hmac("HmacSHA1", k, data),
                "HmacSHA256" to Crypto.hmac("HmacSHA256", k, data),
                "HmacSHA512" to Crypto.hmac("HmacSHA512", k, data)
            )
            out.setText(rows.joinToString("\n") { (n, b) ->
                "$n: ${Crypto.toHex(b, lower)}"
            })
        }
        addView(row(btn))

        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
