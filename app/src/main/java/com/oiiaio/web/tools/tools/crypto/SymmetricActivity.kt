package com.oiiaio.web.tools.tools.crypto

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.CodecActivity
import com.oiiaio.web.tools.util.Crypto
import java.security.SecureRandom
import javax.crypto.Cipher

abstract class SymmetricActivity : CodecActivity() {

    protected abstract val algo: String
    protected abstract val validKeySizes: IntArray
    private val isAes get() = algo == "AES"

    private var ecb = false
    private var useHex = false
    private lateinit var keyEt: EditText
    private lateinit var ivEt: EditText
    private lateinit var ivHost: LinearLayout

    override val encodeLabel get() = "加密"
    override val decodeLabel get() = "解密"
    override fun monoInput() = false

    private val ivLen get() = if (isAes) 16 else 8

    private fun randomAscii(n: Int): String {
        val pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val r = SecureRandom()
        return (1..n).map { pool[r.nextInt(pool.length)] }.joinToString("")
    }

    private fun keyBytes(): ByteArray {
        val key = keyEt.text.toString().toByteArray(Charsets.UTF_8)
        require(key.size in validKeySizes) {
            "密钥长度需为 ${validKeySizes.joinToString("/")} 字节，当前为 ${key.size}"
        }
        return key
    }

    private fun ivBytes(): ByteArray? {
        if (ecb) return null
        val iv = ivEt.text.toString().toByteArray(Charsets.UTF_8)
        require(iv.size == ivLen) { "IV 必须为 $ivLen 字节，当前为 ${iv.size}" }
        return iv
    }

    private fun run(mode: Int, data: ByteArray): ByteArray =
        if (isAes) Crypto.aes(ecb, mode, keyBytes(), ivBytes(), data)
        else Crypto.des(ecb, mode, keyBytes(), ivBytes(), data)

    override fun extra(container: LinearLayout) {
        container.addView(labelText("加密模式"))
        container.addView(segmented(listOf("CBC", "ECB")) { idx, _ ->
            ecb = idx == 1
            ivHost.visibility = if (ecb) View.GONE else View.VISIBLE
        })

        container.addView(labelText("输出格式"))
        container.addView(segmented(listOf("Base64", "Hex"), initial = 0) { idx, _ ->
            useHex = idx == 1
        })

        container.addView(labelText("密钥（${validKeySizes.joinToString("/")} 字节，UTF-8）"))
        keyEt = input("请输入 $algo 密钥", mono = true)
        container.addView(keyEt)

        ivHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        ivHost.addView(labelText("初始向量 IV（$ivLen 字节）"))
        ivEt = input("请输入 IV", mono = true)
        ivHost.addView(ivEt)
        container.addView(ivHost)

        val random = ghostBtn("🎲  随机生成密钥与 IV") {
            keyEt.setText(randomAscii(validKeySizes.first()))
            ivEt.setText(randomAscii(ivLen))
        }
        container.addView(row(random))
    }

    override fun encode(s: String): String {
        val raw = run(Cipher.ENCRYPT_MODE, s.toByteArray(Charsets.UTF_8))
        return if (useHex) Crypto.toHex(raw) else Crypto.b64(raw)
    }

    override fun decode(s: String): String {
        val raw = if (useHex) Crypto.fromHex(s) else Crypto.unb64(s)
        return String(run(Cipher.DECRYPT_MODE, raw), Charsets.UTF_8)
    }
}
