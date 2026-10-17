package com.oiiaio.web.tools.tools.text

import com.oiiaio.web.tools.ui.BaseToolActivity
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class JsonActivity : BaseToolActivity() {
    override fun toolTitle() = "JSON 格式化 / 校验"

    override fun onBuild() {
        label("输入 JSON")
        val inp = input("粘贴 JSON 内容", multi = true, mono = true, minLines = 6)
        addView(inp)

        val out = output("格式化结果", minLines = 8)

        fun handle(mode: Int) {
            val raw = inp.text.toString().trim()
            if (raw.isEmpty()) {
                toast("请输入 JSON")
                return
            }
            try {
                when (val v = JSONTokener(raw).nextValue()) {
                    is JSONObject -> out.setText(
                        when (mode) {
                            0 -> v.toString(2)
                            1 -> v.toString()
                            else -> "✓ 合法 JSON 对象，包含 ${v.length()} 个键"
                        }
                    )
                    is JSONArray -> out.setText(
                        when (mode) {
                            0 -> v.toString(2)
                            1 -> v.toString()
                            else -> "✓ 合法 JSON 数组，包含 ${v.length()} 个元素"
                        }
                    )
                    else -> out.setText("✗ 不是标准的 JSON 对象或数组")
                }
            } catch (e: Exception) {
                out.setText("✗ JSON 解析失败：${e.message}")
            }
        }

        addView(row(
            primaryBtn("格式化") { handle(0) },
            ghostBtn("压缩") { handle(1) },
            ghostBtn("校验") { handle(2) }
        ))
        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
