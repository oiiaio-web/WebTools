package com.oiiaio.web.tools.tools.crypto

import android.widget.EditText
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import com.oiiaio.web.tools.util.Crypto
import java.security.KeyPair

class RsaActivity : BaseToolActivity() {
    override fun toolTitle() = "RSA 加解密与签名"

    private var pair: KeyPair? = null
    private var keyBits = 2048

    private lateinit var pubOut: EditText
    private lateinit var privOut: EditText
    private lateinit var encIn: EditText
    private lateinit var encOut: EditText
    private lateinit var decIn: EditText
    private lateinit var decOut: EditText
    private lateinit var signIn: EditText
    private lateinit var signOut: EditText

    private fun requirePair(): KeyPair = pair ?: throw IllegalStateException("请先生成密钥对")

    override fun onBuild() {
        // ① key pair
        label("① 生成密钥对", 4)
        card {
            addView(dropdown(listOf("2048 位（推荐）", "1024 位（较快）"), 0) { idx, _ ->
                keyBits = if (idx == 0) 2048 else 1024
            })
            addView(row(primaryBtn("生成 RSA 密钥对") {
                try {
                    pair = Crypto.genKeyPair("RSA", keyBits)
                    pubOut.setText(Crypto.b64(requirePair().public.encoded))
                    privOut.setText(Crypto.b64(requirePair().private.encoded))
                    toast("已生成 $keyBits 位密钥对")
                } catch (e: Exception) {
                    toast("生成失败：${e.message}")
                }
            }))
            addView(labelText("公钥（Public Key，Base64）"))
            pubOut = output("公钥", minLines = 3)
            addView(pubOut)
            addView(labelText("私钥（Private Key，Base64）"))
            privOut = output("私钥", minLines = 6)
            addView(privOut)
            addView(row(
                ghostBtn("复制公钥") { if (pubOut.text.isNotEmpty()) copy(pubOut.text.toString(), "公钥") },
                ghostBtn("复制私钥") { if (privOut.text.isNotEmpty()) copy(privOut.text.toString(), "私钥") }
            ))
        }

        // ② encrypt
        label("② 公钥加密")
        card {
            encIn = input("输入要加密的明文", multi = true, mono = false, minLines = 3)
            addView(encIn)
            addView(row(primaryBtn("公钥加密") {
                try {
                    val data = encIn.text.toString().toByteArray(Charsets.UTF_8)
                    encOut.setText(Crypto.b64(Crypto.rsaEncrypt(requirePair().public, data, keyBits)))
                } catch (e: Exception) {
                    toast("加密失败：${e.message}")
                }
            }))
            encOut = output("密文（Base64）", minLines = 3)
            addView(encOut)
            addView(resultActions(encOut))
        }

        // ③ decrypt
        label("③ 私钥解密")
        card {
            decIn = input("输入 Base64 密文", multi = true, minLines = 3)
            addView(decIn)
            addView(row(primaryBtn("私钥解密") {
                try {
                    val raw = Crypto.unb64(decIn.text.toString())
                    decOut.setText(String(Crypto.rsaDecrypt(requirePair().private, raw, keyBits), Charsets.UTF_8))
                } catch (e: Exception) {
                    toast("解密失败：${e.message}")
                }
            }))
            decOut = output("解密后的明文", minLines = 3)
            addView(decOut)
            addView(resultActions(decOut))
        }

        // ④ sign / verify
        label("④ 数字签名（SHA256withRSA）")
        card {
            signIn = input("输入要签名的内容", multi = true, mono = false, minLines = 2)
            addView(signIn)
            addView(row(
                primaryBtn("生成签名") {
                    try {
                        val data = signIn.text.toString().toByteArray(Charsets.UTF_8)
                        signOut.setText(Crypto.b64(Crypto.rsaSign(requirePair().private, data)))
                    } catch (e: Exception) {
                        toast("签名失败：${e.message}")
                    }
                },
                ghostBtn("验证签名") {
                    try {
                        val data = signIn.text.toString().toByteArray(Charsets.UTF_8)
                        val sig = Crypto.unb64(signOut.text.toString())
                        val ok = Crypto.rsaVerify(requirePair().public, data, sig)
                        toast(if (ok) "✓ 签名验证通过" else "✗ 签名验证失败")
                    } catch (e: Exception) {
                        toast("验证失败：${e.message}")
                    }
                }
            ))
            signOut = output("签名（Base64）", minLines = 3)
            addView(signOut)
            addView(resultActions(signOut))
        }
        note("RSA 适合加密短数据或对称密钥；长文本请使用 AES。本工具已自动分块处理。")
    }
}
