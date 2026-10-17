package com.oiiaio.web.tools.tools.crypto

import com.oiiaio.web.tools.ui.BaseToolActivity
import com.oiiaio.web.tools.util.Crypto

class HashActivity : BaseToolActivity() {
    override fun toolTitle() = "哈希计算（MD5 / SHA）"

    override fun onBuild() {
        label("输入内容")
        val inp = input("要计算哈希的文本或字符串", multi = true, mono = false, minLines = 4)
        var lower = false
        addView(switchRow("使用小写字母输出") { lower = it })

        val out = output("哈希结果", minLines = 7)
        val btn = primaryBtn("计算全部哈希") {
            val bytes = inp.text.toString().toByteArray(Charsets.UTF_8)
            val rows = listOf(
                "MD5" to Crypto.digest("MD5", bytes),
                "SHA-1" to Crypto.digest("SHA-1", bytes),
                "SHA-256" to Crypto.digest("SHA-256", bytes),
                "SHA-384" to Crypto.digest("SHA-384", bytes),
                "SHA-512" to Crypto.digest("SHA-512", bytes)
            )
            val crc = Crypto.crc32(bytes)
            out.setText(buildString {
                rows.forEach { (n, b) -> append("$n: ${Crypto.toHex(b, lower)}\n") }
                val crcHex = crc.toString(16).let { if (lower) it.lowercase() else it.uppercase() }
                append("CRC32: $crcHex")
            })
        }
        addView(row(btn))

        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
