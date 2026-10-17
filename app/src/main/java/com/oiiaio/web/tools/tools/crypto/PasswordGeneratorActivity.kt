package com.oiiaio.web.tools.tools.crypto

import android.view.ViewGroup
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.security.SecureRandom

class PasswordGeneratorActivity : BaseToolActivity() {
    override fun toolTitle() = "随机密码生成器"

    private val lower = "abcdefghijklmnopqrstuvwxyz".toList()
    private val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()
    private val digits = "0123456789".toList()
    private val special = "!@#$%^&*()-_=+[]{};:,.?/".toList()
    private val ambiguous = setOf('0', 'O', 'o', '1', 'l', 'I', 'i')

    private val rnd = SecureRandom()

    private fun gen(length: Int, pools: List<List<Char>>, excludeAmb: Boolean): String {
        val usable = pools.map { p -> p.filterNot { excludeAmb && it in ambiguous } }.filter { it.isNotEmpty() }
        require(usable.isNotEmpty()) { "至少选择一种字符类型" }
        val all = usable.flatten()
        val picked = ArrayList<Char>()
        usable.forEach { p -> if (picked.size < length) picked.add(p[rnd.nextInt(p.size)]) }
        while (picked.size < length) picked.add(all[rnd.nextInt(all.size)])
        picked.shuffle(rnd)
        return picked.joinToString("")
    }

    override fun onBuild() {
        label("长度与数量")
        val lenInput = input("长度（默认 16）", numeric = true)
        val cntInput = input("条数（默认 5）", numeric = true)
        lenInput.setText("16")
        cntInput.setText("5")
        lenInput.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            .apply { marginEnd = dp(8f) }
        cntInput.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        val numRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10f) }
        }
        numRow.addView(lenInput)
        numRow.addView(cntInput)
        addView(numRow)

        var useUpper = true
        var useLower = true
        var useDigit = true
        var useSpecial = true
        var excludeAmb = false
        addView(switchRow("包含大写字母 A-Z") { useUpper = it })
        addView(switchRow("包含小写字母 a-z") { useLower = it })
        addView(switchRow("包含数字 0-9") { useDigit = it })
        addView(switchRow("包含特殊符号 !@#$%…") { useSpecial = it })
        addView(switchRow("排除易混淆字符 0 O 1 l I") { excludeAmb = it })

        val out = output("生成的密码", minLines = 6)
        val btn = primaryBtn("🎲  生成密码") {
            val len = lenInput.text.toString().ifBlank { "16" }.toIntOrNull()?.coerceIn(1, 128) ?: 16
            val count = cntInput.text.toString().ifBlank { "5" }.toIntOrNull()?.coerceIn(1, 100) ?: 5
            val pools = buildList {
                if (useUpper) add(upper)
                if (useLower) add(lower)
                if (useDigit) add(digits)
                if (useSpecial) add(special)
            }
            try {
                out.setText((1..count).joinToString("\n") { gen(len, pools, excludeAmb) })
            } catch (e: Exception) {
                toast(e.message ?: "生成失败")
            }
        }
        addView(row(btn))
        label("结果（点击可全选复制）")
        addView(out)
        addView(resultActions(out))
    }
}
