package com.oiiaio.web.tools.util

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.zip.CRC32
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    private val HEX = "0123456789ABCDEF".toCharArray()

    fun toHex(bytes: ByteArray, lower: Boolean = false): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val v = b.toInt() and 0xFF
            sb.append(HEX[v ushr 4]).append(HEX[v and 0x0F])
        }
        return if (lower) sb.toString().lowercase() else sb.toString()
    }

    fun fromHex(hex: String): ByteArray {
        val clean = hex.replace(Regex("[^0-9a-fA-F]"), "")
        return ByteArray(clean.length / 2) {
            clean.substring(it * 2, it * 2 + 2).toInt(16).toByte()
        }
    }

    fun b64(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
    fun unb64(s: String): ByteArray = Base64.decode(s.trim(), Base64.NO_WRAP)

    fun digest(alg: String, data: ByteArray): ByteArray =
        MessageDigest.getInstance(alg).digest(data)

    fun crc32(data: ByteArray): Long {
        val c = CRC32()
        c.update(data)
        return c.value
    }

    fun hmac(alg: String, key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance(alg)
        mac.init(SecretKeySpec(key, alg))
        return mac.doFinal(data)
    }

    private fun symmetric(
        algo: String,
        block: String,
        ecb: Boolean,
        mode: Int,
        key: ByteArray,
        iv: ByteArray?,
        data: ByteArray
    ): ByteArray {
        val cipher = Cipher.getInstance("$algo/${if (ecb) "ECB" else "CBC"}/$block")
        val keySpec = SecretKeySpec(key, algo)
        if (ecb) cipher.init(mode, keySpec)
        else cipher.init(mode, keySpec, IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    fun aes(ecb: Boolean, mode: Int, key: ByteArray, iv: ByteArray?, data: ByteArray) =
        symmetric("AES", "PKCS5Padding", ecb, mode, key, iv, data)

    fun des(ecb: Boolean, mode: Int, key: ByteArray, iv: ByteArray?, data: ByteArray) =
        symmetric("DES", "PKCS5Padding", ecb, mode, key, iv, data)

    fun genKeyPair(algo: String, size: Int): KeyPair {
        val gen = KeyPairGenerator.getInstance(algo)
        gen.initialize(size, SecureRandom())
        return gen.generateKeyPair()
    }

    fun rsaPublicKey(b64: String): PublicKey {
        val bytes = unb64(b64)
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(bytes))
    }

    fun rsaPrivateKey(b64: String): PrivateKey {
        val bytes = unb64(b64)
        return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(bytes))
    }

    private fun rsaBlock(mode: Int, key: java.security.Key, data: ByteArray, blockSize: Int): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(mode, key)
        val out = ByteArrayOutputStream()
        var off = 0
        while (off < data.size) {
            val len = minOf(blockSize, data.size - off)
            out.write(cipher.doFinal(data, off, len))
            off += len
        }
        return out.toByteArray()
    }

    /** PKCS1Padding encrypt block = keyBytes - 11 */
    fun rsaEncrypt(pub: PublicKey, data: ByteArray, keyBits: Int = 2048): ByteArray =
        rsaBlock(Cipher.ENCRYPT_MODE, pub, data, keyBits / 8 - 11)

    fun rsaDecrypt(priv: PrivateKey, data: ByteArray, keyBits: Int = 2048): ByteArray =
        rsaBlock(Cipher.DECRYPT_MODE, priv, data, keyBits / 8)

    fun rsaSign(priv: PrivateKey, data: ByteArray): ByteArray {
        val s = Signature.getInstance("SHA256withRSA")
        s.initSign(priv)
        s.update(data)
        return s.sign()
    }

    fun rsaVerify(pub: PublicKey, data: ByteArray, sig: ByteArray): Boolean {
        val s = Signature.getInstance("SHA256withRSA")
        s.initVerify(pub)
        s.update(data)
        return s.verify(sig)
    }
}
