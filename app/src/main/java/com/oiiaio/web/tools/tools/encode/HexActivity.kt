package com.oiiaio.web.tools.tools.encode

import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.CodecActivity

class HexActivity : CodecActivity() {
    override fun toolTitle() = "十六进制 HEX 编解码"
    private var spaced = false

    override fun extra(container: LinearLayout) {
        container.addView(switchRow("字节间使用空格分隔（如 48 65 6C）") { spaced = it })
    }

    override fun encode(s: String): String {
        val bytes = s.toByteArray(Charsets.UTF_8)
        return bytes.joinToString(if (spaced) " " else "") { "%02X".format(it) }
    }

    override fun decode(s: String): String {
        val clean = s.replace(Regex("[^0-9a-fA-F]"), "")
        require(clean.length % 2 == 0) { "HEX 字符数量必须为偶数，当前为 ${clean.length}" }
        val bytes = ByteArray(clean.length / 2) {
            clean.substring(it * 2, it * 2 + 2).toInt(16).toByte()
        }
        return String(bytes, Charsets.UTF_8)
    }
}
