package com.oiiaio.web.tools.tools.web

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.oiiaio.web.tools.ui.BaseToolActivity

class ImageBase64Activity : BaseToolActivity() {
    override fun toolTitle() = "图片 / Base64 互转"

    private var prefix = true

    private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) handlePicked(uri)
    }
    private lateinit var out: android.widget.EditText

    private fun handlePicked(uri: Uri) {
        try {
            val stream = contentResolver.openInputStream(uri)
                ?: throw java.io.IOException("无法打开图片")
            val bytes = stream.use { it.readBytes() }
            val mime = contentResolver.getType(uri) ?: "image/png"
            val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            out.setText(if (prefix) "data:$mime;base64,$b64" else b64)
            toast("已转换，大小 %.1f KB".format(bytes.size / 1024.0))
        } catch (e: Exception) {
            toast("读取图片失败：${e.message}")
        }
    }

    override fun onBuild() {
        out = output("图片转换后的 Base64（data URI）", minLines = 5)

        label("图片 → Base64", 4)
        card {
            addView(switchRow("包含 data:image/...;base64 前缀", true) { prefix = it })
            addView(row(primaryBtn("🖼  选择图片") {
                picker.launch("image/*")
            }))
        }
        label("输出（可直接用于 <img src=...>）")
        addView(out)
        addView(resultActions(out))

        label("Base64 → 图片")
        card {
            val inp = input("粘贴 data URI 或纯 Base64", multi = true, mono = true, minLines = 3)
            addView(inp)
            val imageView = ImageView(this@ImageBase64Activity).apply {
                adjustViewBounds = true
                maxWidth = dp(280f)
                maxHeight = dp(280f)
                setBackgroundColor(cSurface2)
                layoutParams = LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = dp(10f)
                }
                visibility = android.view.View.GONE
            }
            addView(imageView)
            addView(row(primaryBtn("解码显示图片") {
                val raw = inp.text.toString().trim().substringAfter("base64,", "")
                    .replace(Regex("\\s"), "")
                try {
                    val bytes = Base64.decode(raw, Base64.DEFAULT)
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bmp == null) {
                        toast("不是有效的图片 Base64")
                    } else {
                        imageView.setImageBitmap(bmp)
                        imageView.visibility = android.view.View.VISIBLE
                    }
                } catch (e: Exception) {
                    toast("解码失败：${e.message}")
                }
            }))
        }
    }
}
