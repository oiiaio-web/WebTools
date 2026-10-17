package com.oiiaio.web.tools.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oiiaio.web.tools.R
import com.oiiaio.web.tools.ThemeEngine

abstract class BaseActivity : AppCompatActivity() {

    val cBg by lazy { ThemeEngine.attrColor(this, R.attr.cBg) }
    val cBg2 by lazy { ThemeEngine.attrColor(this, R.attr.cBg2) }
    val cSurface by lazy { ThemeEngine.attrColor(this, R.attr.cSurface) }
    val cSurface2 by lazy { ThemeEngine.attrColor(this, R.attr.cSurface2) }
    val cPrimary by lazy { ThemeEngine.attrColor(this, R.attr.cPrimary) }
    val cAccent by lazy { ThemeEngine.attrColor(this, R.attr.cAccent) }
    val cText by lazy { ThemeEngine.attrColor(this, R.attr.cText) }
    val cSub by lazy { ThemeEngine.attrColor(this, R.attr.cSubText) }
    val cStroke by lazy { ThemeEngine.attrColor(this, R.attr.cStroke) }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeEngine.apply(this)
        super.onCreate(savedInstanceState)
    }

    protected fun dp(v: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics).toInt()

    protected fun dp(v: Int): Int = dp(v.toFloat())

    protected fun toast(msg: String?) {
        Toast.makeText(this, msg ?: "null", Toast.LENGTH_SHORT).show()
    }

    protected fun copy(text: String, label: String = "已复制") {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("webtools", text))
        toast("${label}到剪贴板")
    }

    protected fun share(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "分享"))
    }
}
