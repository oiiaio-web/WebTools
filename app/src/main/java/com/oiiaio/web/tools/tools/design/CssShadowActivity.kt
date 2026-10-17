package com.oiiaio.web.tools.tools.design

import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.FrameLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import com.oiiaio.web.tools.ui.gradStroke

class CssShadowActivity : BaseToolActivity() {
    override fun toolTitle() = "CSS 阴影生成器"

    private var inset = false
    private lateinit var webView: WebView
    private lateinit var out: EditText
    private lateinit var fields: Map<String, EditText>

    private fun num(key: String, default: Int): Int =
        fields[key]?.text?.toString()?.toIntOrNull() ?: default

    override fun onBuild() {
        label("实时预览", 4)
        val container = FrameLayout(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(190f)
            ).apply { bottomMargin = dp(12f) }
            background = gradStroke(cSurface, cStroke, 16f)
        }
        webView = WebView(this)
        container.addView(webView)
        addView(container)

        card {
            addView(labelText("偏移 / 模糊 / 扩散（px）"))
            val f = HashMap<String, EditText>()
            fun numField(hint: String, value: String): EditText =
                input(hint, numeric = true).apply {
                    setText(value)
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                    ).apply { marginEnd = dp(8f) }
                }
            val row1 = android.widget.LinearLayout(this@CssShadowActivity).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10f) }
            }
            val x = numField("X 偏移", "0"); val y = numField("Y 偏移", "8")
            val blur = numField("模糊", "24")
            row1.addView(x); row1.addView(y); row1.addView(blur)
            addView(row1)

            val row2 = android.widget.LinearLayout(this@CssShadowActivity).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10f) }
            }
            val spread = numField("扩散", "0"); val radius = numField("圆角", "18")
            val alpha = numField("不透明度%", "28")
            row2.addView(spread); row2.addView(radius); row2.addView(alpha)
            addView(row2)

            f.putAll(mapOf("x" to x, "y" to y, "blur" to blur, "spread" to spread,
                "radius" to radius, "alpha" to alpha))
            fields = f

            addView(labelText("阴影颜色（HEX）"))
            val colorInput = input("#000000", mono = true)
            addView(colorInput)
            addView(switchRow("内阴影 inset") { inset = it })
            addView(row(primaryBtn("生成 / 刷新") {
                update(colorInput.text.toString().trim())
            }))
        }

        label("CSS 代码")
        out = output("box-shadow: ...", minLines = 2)
        addView(out)
        addView(resultActions(out))
        update("#000000")
    }

    private fun update(hex: String) {
        val color = try {
            Color.parseColor(hex)
        } catch (e: Exception) {
            Color.BLACK
        }
        val x = num("x", 0); val y = num("y", 8); val blur = num("blur", 24)
        val spread = num("spread", 0); val radius = num("radius", 18)
        val a = (num("alpha", 28).coerceIn(0, 100) / 100f)
        val rgba = "rgba(${Color.red(color)},${Color.green(color)},${Color.blue(color)},$a)"
        val prefix = if (inset) "inset " else ""
        val shadow = "$prefix${x}px ${y}px ${blur}px ${spread}px $rgba"
        out.setText("box-shadow: $shadow;")

        val html = """<!DOCTYPE html><html><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<style>
html,body{margin:0;height:100%;background:#E9EAF1;}
body{display:flex;align-items:center;justify-content:center;}
.box{width:150px;height:150px;background:#FFFFFF;border-radius:${radius}px;box-shadow:$shadow;}
</style></head><body><div class="box"></div></body></html>"""
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    override fun onDestroy() {
        (webView.parent as? FrameLayout)?.removeView(webView)
        webView.destroy()
        super.onDestroy()
    }
}
