package com.oiiaio.web.tools.tools.encode

import com.oiiaio.web.tools.ui.CodecActivity
import java.io.ByteArrayOutputStream

class Base32Activity : CodecActivity() {
    override fun toolTitle() = "Base32 编解码（RFC 4648）"

    private val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    override fun encode(s: String): String {
        val data = s.toByteArray(Charsets.UTF_8)
        var buffer = 0
        var bitsLeft = 0
        val sb = StringBuilder()
        for (b in data) {
            buffer = (buffer shl 8) or (b.toInt() and 0xFF)
            bitsLeft += 8
            while (bitsLeft >= 5) {
                sb.append(alphabet[(buffer shr (bitsLeft - 5)) and 0x1F])
                bitsLeft -= 5
            }
        }
        if (bitsLeft > 0) {
            sb.append(alphabet[(buffer shl (5 - bitsLeft)) and 0x1F])
        }
        while (sb.length % 8 != 0) sb.append('=')
        return sb.toString()
    }

    override fun decode(s: String): String {
        val clean = s.trim().uppercase().replace("=", "").replace(Regex("\\s"), "")
        var buffer = 0
        var bitsLeft = 0
        val out = ByteArrayOutputStream()
        for (c in clean) {
            val idx = alphabet.indexOf(c)
            require(idx >= 0) { "非法 Base32 字符：$c" }
            buffer = (buffer shl 5) or idx
            bitsLeft += 5
            if (bitsLeft >= 8) {
                out.write((buffer shr (bitsLeft - 8)) and 0xFF)
                bitsLeft -= 8
            }
        }
        return String(out.toByteArray(), Charsets.UTF_8)
    }
}
