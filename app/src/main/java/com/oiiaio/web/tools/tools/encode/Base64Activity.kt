package com.oiiaio.web.tools.tools.encode

import android.util.Base64
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.CodecActivity

class Base64Activity : CodecActivity() {
    override fun toolTitle() = "Base64 编解码"
    private var urlSafe = false

    private fun flag(): Int {
        var f = Base64.NO_WRAP
        if (urlSafe) f = f or Base64.URL_SAFE
        return f
    }

    override fun extra(container: LinearLayout) {
        container.addView(switchRow("URL Safe 模式（用 - 与 _ 替换 + 与 /）") { urlSafe = it })
    }

    override fun encode(s: String): String =
        Base64.encodeToString(s.toByteArray(Charsets.UTF_8), flag())

    override fun decode(s: String): String =
        String(Base64.decode(s.trim(), flag()), Charsets.UTF_8)
}
