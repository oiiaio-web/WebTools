package com.oiiaio.web.tools.tools.web

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.io.File

class QrGeneratorActivity : BaseToolActivity() {
    override fun toolTitle() = "二维码生成"

    private val levels = listOf(
        "L（约 7%）" to ErrorCorrectionLevel.L,
        "M（约 15%）" to ErrorCorrectionLevel.M,
        "Q（约 25%）" to ErrorCorrectionLevel.Q,
        "H（约 30%）" to ErrorCorrectionLevel.H
    )
    private var level = ErrorCorrectionLevel.M
    private var bitmap: Bitmap? = null

    private fun makeQr(text: String, sizePx: Int, ec: ErrorCorrectionLevel): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ec,
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        val pixels = IntArray(sizePx * sizePx) { i ->
            if (matrix.get(i % sizePx, i / sizePx)) Color.BLACK else Color.WHITE
        }
        return Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, sizePx, 0, 0, sizePx, sizePx)
        }
    }

    override fun onBuild() {
        label("输入内容（网址 / 文本）")
        val content = input("例如 https://example.com 或任意文本", multi = true, mono = false, minLines = 3)
        addView(content)

        addView(labelText("容错等级（等级越高越耐污损）"))
        addView(segmented(listOf("L", "M", "Q", "H"), initial = 1) { idx, _ ->
            level = levels[idx].second
        })

        val imageView = ImageView(this).apply {
            setBackgroundColor(Color.WHITE)
            setPadding(dp(10f), dp(10f), dp(10f), dp(10f))
            layoutParams = LinearLayout.LayoutParams(dp(240f), dp(240f)).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
        val placeholder = TextView(this).apply {
            text = "生成后在此显示二维码"
            setTextColor(cSub)
            gravity = Gravity.CENTER
            textSize = 13f
        }
        label("二维码预览")
        card {
            val center = LinearLayout(this@QrGeneratorActivity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
            }
            center.addView(placeholder)
            center.addView(imageView)
            imageView.visibility = android.view.View.GONE
            addView(center)
        }

        addView(row(
            primaryBtn("生成二维码") {
                val text = content.text.toString().trim()
                if (text.isEmpty()) {
                    toast("请输入内容")
                    return@primaryBtn
                }
                try {
                    bitmap = makeQr(text, 720, level)
                    imageView.setImageBitmap(bitmap)
                    imageView.visibility = android.view.View.VISIBLE
                    placeholder.visibility = android.view.View.GONE
                } catch (e: Exception) {
                    toast("内容过长，生成失败")
                }
            },
            ghostBtn("分享图片") {
                val bmp = bitmap
                if (bmp == null) {
                    toast("请先生成二维码")
                } else {
                    shareQr(bmp)
                }
            }
        ))
        note("二维码为黑白标准格式，可被微信 / 支付宝 / 系统相机等主流工具识别。")
    }

    private fun shareQr(bmp: Bitmap) {
        try {
            val file = File(cacheDir, "qr_${System.currentTimeMillis()}.png")
            file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "分享二维码"))
        } catch (e: Exception) {
            toast("分享失败：${e.message}")
        }
    }
}
