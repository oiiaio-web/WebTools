package com.oiiaio.web.tools.tools.crypto

class DesActivity : SymmetricActivity() {
    override fun toolTitle() = "DES 加解密"
    override val algo = "DES"
    override val validKeySizes = intArrayOf(8)
}
