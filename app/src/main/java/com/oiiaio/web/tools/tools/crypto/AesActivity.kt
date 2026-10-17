package com.oiiaio.web.tools.tools.crypto

class AesActivity : SymmetricActivity() {
    override fun toolTitle() = "AES 加解密"
    override val algo = "AES"
    override val validKeySizes = intArrayOf(16, 24, 32)
}
