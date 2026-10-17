package com.oiiaio.web.tools.tools.encode

import com.oiiaio.web.tools.ui.CodecActivity
import java.net.URLDecoder
import java.net.URLEncoder

class UrlEncodeActivity : CodecActivity() {
    override fun toolTitle() = "URL 编解码"
    override fun encode(s: String): String = URLEncoder.encode(s, "UTF-8")
    override fun decode(s: String): String = URLDecoder.decode(s, "UTF-8")
}
