package com.oiiaio.web.tools.tools.encode

import android.text.InputType
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.math.BigInteger

class RadixActivity : BaseToolActivity() {
    override fun toolTitle() = "进制转换"

    private val radixes = listOf(2, 8, 10, 16)
    private var radix = 10

    override fun onBuild() {
        label("输入数值（按下方所选进制解析）")
        val inp = input("请输入要转换的数字", mono = true)
        inp.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        val seg = segmented(listOf("二进制", "八进制", "十进制", "十六进制"), initial = 2) { idx, _ ->
            radix = radixes[idx]
        }
        addView(seg)

        val out = output("转换结果", minLines = 5)
        val convert = primaryBtn("转换") {
            val raw = inp.text.toString().trim().replace(Regex("\\s"), "")
            if (raw.isEmpty()) {
                toast("请输入数值")
                return@primaryBtn
            }
            try {
                val negative = raw.startsWith("-")
                val body = if (negative) raw.substring(1) else raw
                val v = BigInteger(body, radix).let { if (negative) it.negate() else it }
                out.setText(buildString {
                    append("二进制：${v.toString(2)}\n")
                    append("八进制：${v.toString(8)}\n")
                    append("十进制：${v.toString(10)}\n")
                    append("十六进制：${v.toString(16).uppercase()}")
                })
            } catch (e: Exception) {
                out.setText("⚠️ 解析失败：不是有效的 ${radix} 进制数字")
                toast("格式错误")
            }
        }
        addView(row(convert))
        label("输出结果")
        addView(out)
        addView(resultActions(out))
    }
}
