package com.oiiaio.web.tools.tools.design

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import com.oiiaio.web.tools.ui.grad

class CssGradientActivity : BaseToolActivity() {
    override fun toolTitle() = "CSS 渐变生成器"

    private data class Dir(val label: String, val css: Int, val orientation: GradientDrawable.Orientation)

    private val dirs = listOf(
        Dir("0° 向上", 0, GradientDrawable.Orientation.BOTTOM_TOP),
        Dir("45° 右上", 45, GradientDrawable.Orientation.BL_TR),
        Dir("90° 向右", 90, GradientDrawable.Orientation.LEFT_RIGHT),
        Dir("135° 右下", 135, GradientDrawable.Orientation.TL_BR),
        Dir("180° 向下", 180, GradientDrawable.Orientation.TOP_BOTTOM),
        Dir("270° 向左", 270, GradientDrawable.Orientation.RIGHT_LEFT)
    )
    private var dirIndex = 3

    private fun build(): Triple<Int, Int, Int>? {
        return try {
            val c1 = Color.parseColor(c1Input.text.toString().trim())
            val c2 = Color.parseColor(c2Input.text.toString().trim())
            Triple(dirs[dirIndex].css, c1, c2)
        } catch (e: Exception) {
            toast("颜色 HEX 格式错误，如 #4E54C8")
            null
        }
    }

    private lateinit var c1Input: android.widget.EditText
    private lateinit var c2Input: android.widget.EditText
    private lateinit var preview: LinearLayout
    private lateinit var out: android.widget.EditText

    override fun onBuild() {
        label("预览", 4)
        preview = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(160f)
            ).apply { bottomMargin = dp(12f) }
            background = grad(cSurface, cStroke, 16f)
        }
        addView(preview)

        card {
            addView(labelText("起始颜色（HEX）"))
            c1Input = input("#4E54C8", mono = true)
            addView(c1Input)
            addView(labelText("结束颜色（HEX）"))
            c2Input = input("#8E2DE2", mono = true)
            addView(c2Input)
            addView(labelText("渐变方向"))
            addView(dropdown(dirs.map { it.label }, dirIndex) { idx, _ ->
                dirIndex = idx
                update()
            })
            addView(row(primaryBtn("生成 / 刷新") { update() }))
        }

        label("CSS 代码")
        out = output("background: linear-gradient(...)", minLines = 3)
        addView(out)
        addView(resultActions(out))
        update()
    }

    private fun update() {
        val b = build() ?: return
        val (cssAngle, c1, c2) = b
        val gd = GradientDrawable(dirs[dirIndex].orientation, intArrayOf(c1, c2)).apply {
            cornerRadius = dp(16f).toFloat()
        }
        preview.background = gd
        fun h(c: Int) = "#%02X%02X%02X".format(
            Color.red(c), Color.green(c), Color.blue(c)
        )
        out.setText("background: linear-gradient(${cssAngle}deg, ${h(c1)}, ${h(c2)});")
    }
}
