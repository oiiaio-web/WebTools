package com.oiiaio.web.tools.tools.design

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.oiiaio.web.tools.ui.BaseToolActivity
import com.oiiaio.web.tools.ui.grad
import kotlin.math.roundToInt

class ColorActivity : BaseToolActivity() {
    override fun toolTitle() = "颜色转换 / 取色"

    private var cr = 0x4E
    private var cg = 0x54
    private var cb = 0xC8

    private lateinit var preview: LinearLayout
    private lateinit var hexLabel: TextView
    private lateinit var hexInput: EditText
    private lateinit var rInput: EditText
    private lateinit var gInput: EditText
    private lateinit var bInput: EditText
    private lateinit var hInput: EditText
    private lateinit var sInput: EditText
    private lateinit var lInput: EditText

    private fun hex() = "#%02X%02X%02X".format(cr, cg, cb)

    private fun rgbToHsl(): Triple<Int, Int, Int> {
        val rf = cr / 255f; val gf = cg / 255f; val bf = cb / 255f
        val max = maxOf(rf, gf, bf); val min = minOf(rf, gf, bf)
        val l = (max + min) / 2f
        var h = 0f; var s = 0f
        if (max != min) {
            val d = max - min
            s = if (l > 0.5f) d / (2 - max - min) else d / (max + min)
            h = when (max) {
                rf -> (gf - bf) / d + if (gf < bf) 6 else 0
                gf -> (bf - rf) / d + 2
                else -> (rf - gf) / d + 4
            } * 60
        }
        return Triple(h.roundToInt(), (s * 100).roundToInt(), (l * 100).roundToInt())
    }

    private fun hslToRgb(h: Int, s: Int, l: Int): Triple<Int, Int, Int> {
        val hh = (h % 360) / 360f; val ss = s / 100f; val ll = l / 100f
        if (ss == 0f) {
            val v = (ll * 255).roundToInt(); return Triple(v, v, v)
        }
        val q = if (ll < 0.5f) ll * (1 + ss) else ll + ss - ll * ss
        val p = 2 * ll - q
        fun h2c(tIn: Float): Float {
            var t = tIn
            if (t < 0) t += 1f; if (t > 1) t -= 1f
            return when {
                t < 1f / 6 -> p + (q - p) * 6 * t
                t < 0.5f -> q
                t < 2f / 3 -> p + (q - p) * (2f / 3 - t) * 6
                else -> p
            }
        }
        return Triple(
            (h2c(hh + 1f / 3) * 255).roundToInt(),
            (h2c(hh) * 255).roundToInt(),
            (h2c(hh - 1f / 3) * 255).roundToInt()
        )
    }

    private fun refresh() {
        preview.setBackgroundColor(Color.rgb(cr, cg, cb))
        val lum = 0.299 * cr + 0.587 * cg + 0.114 * cb
        hexLabel.setTextColor(if (lum > 150) Color.BLACK else Color.WHITE)
        hexLabel.text = "${hex()}\nrgb($cr, $cg, $cb)\n" + run {
            val (h, s, l) = rgbToHsl()
            "hsl($h, $s%, $l%)"
        }
        hexInput.setText(hex())
        rInput.setText(cr.toString()); gInput.setText(cg.toString()); bInput.setText(cb.toString())
        val (h, s, l) = rgbToHsl()
        hInput.setText(h.toString()); sInput.setText(s.toString()); lInput.setText(l.toString())
    }

    override fun onBuild() {
        // preview
        preview = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = grad(cPrimary, radiusDp = 16f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(132f)
            ).apply { bottomMargin = dp(12f) }
            isClickable = true
        }
        hexLabel = TextView(this).apply {
            textSize = 16f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setLineSpacing(dp(4f).toFloat(), 1f)
        }
        preview.addView(hexLabel)
        preview.setOnClickListener { copy(hex(), "颜色值") }
        addView(preview)

        // HEX
        card {
            addView(labelText("HEX（如 #4E54C8）"))
            hexInput = input("#RRGGBB", mono = true)
            addView(hexInput)
            addView(row(primaryBtn("应用 HEX") {
                try {
                    var h = hexInput.text.toString().trim().removePrefix("#")
                    if (h.length == 3) h = h.map { "$it$it" }.joinToString("")
                    require(h.length == 6) { "HEX 需 6 位" }
                    cr = h.substring(0, 2).toInt(16)
                    cg = h.substring(2, 4).toInt(16)
                    cb = h.substring(4, 6).toInt(16)
                    refresh()
                } catch (e: Exception) {
                    toast("HEX 格式错误")
                }
            }))
        }
        // RGB
        card {
            addView(labelText("RGB（0-255）"))
            val numRow = LinearLayout(this@ColorActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10f) }
            }
            rInput = input("R", numeric = true); gInput = input("G", numeric = true); bInput = input("B", numeric = true)
            listOf(rInput, gInput, bInput).forEach {
                it.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { marginEnd = dp(8f) }
                numRow.addView(it)
            }
            addView(numRow)
            addView(row(primaryBtn("应用 RGB") {
                cr = rInput.text.toString().toIntOrNull()?.coerceIn(0, 255) ?: cr
                cg = gInput.text.toString().toIntOrNull()?.coerceIn(0, 255) ?: cg
                cb = bInput.text.toString().toIntOrNull()?.coerceIn(0, 255) ?: cb
                refresh()
            }))
        }
        // HSL
        card {
            addView(labelText("HSL（H 0-360，S/L 0-100）"))
            val numRow = LinearLayout(this@ColorActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10f) }
            }
            hInput = input("H", numeric = true); sInput = input("S", numeric = true); lInput = input("L", numeric = true)
            listOf(hInput, sInput, lInput).forEach {
                it.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { marginEnd = dp(8f) }
                numRow.addView(it)
            }
            addView(numRow)
            addView(row(primaryBtn("应用 HSL") {
                val h = hInput.text.toString().toIntOrNull()?.coerceIn(0, 360) ?: 0
                val s = sInput.text.toString().toIntOrNull()?.coerceIn(0, 100) ?: 0
                val l = lInput.text.toString().toIntOrNull()?.coerceIn(0, 100) ?: 0
                val (nr, ng, nb) = hslToRgb(h, s, l)
                cr = nr; cg = ng; cb = nb
                refresh()
            }))
        }
        // palette
        label("常用配色（点击选择）")
        val palette = listOf(
            "#4E54C8", "#8E2DE2", "#E91E63", "#E0667A", "#E0A03C", "#FF9800",
            "#36B6A8", "#4CAF50", "#03A9F4", "#5C8CE8", "#9C27B0", "#607D8B",
            "#F44336", "#795548", "#000000", "#FFFFFF"
        )
        val grid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val perRow = 8
        palette.chunked(perRow).forEach { rowItems ->
            val lr = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowItems.forEach { hx ->
                val sw = TextView(this).apply {
                    setBackgroundColor(Color.parseColor(hx))
                    isClickable = true
                    val dm = resources.displayMetrics.density
                    val s = ((resources.displayMetrics.widthPixels - 28 * dm - 12 * dm) / 8).toInt()
                    layoutParams = LinearLayout.LayoutParams(s, s)
                    setOnClickListener {
                        val h = hx.removePrefix("#")
                        cr = h.substring(0, 2).toInt(16)
                        cg = h.substring(2, 4).toInt(16)
                        cb = h.substring(4, 6).toInt(16)
                        refresh()
                    }
                }
                lr.addView(sw)
            }
            grid.addView(lr)
        }
        addView(grid)
        refresh()
    }
}
