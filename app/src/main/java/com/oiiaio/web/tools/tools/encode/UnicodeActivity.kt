package com.oiiaio.web.tools.tools.encode

import com.oiiaio.web.tools.ui.CodecActivity

class UnicodeActivity : CodecActivity() {
    override fun toolTitle() = "Unicode 转义（\\uXXXX）"

    private val unicodeRegex = Regex("""\\u([0-9a-fA-F]{4})""")

    override fun encode(s: String): String = buildString {
        s.forEach { c ->
            if (c.code > 0x7E) append("\\u%04x".format(c.code)) else append(c)
        }
    }

    override fun decode(s: String): String =
        unicodeRegex.replace(s) { m ->
            String(Character.toChars(m.groupValues[1].toInt(16)))
        }
}
