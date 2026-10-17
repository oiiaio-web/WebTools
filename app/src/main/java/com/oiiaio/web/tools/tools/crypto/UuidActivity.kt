package com.oiiaio.web.tools.tools.crypto

import android.view.ViewGroup
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.util.UUID

class UuidActivity : BaseToolActivity() {
    override fun toolTitle() = "UUID 生成器（v4）"

    override fun onBuild() {
        label("生成数量")
        val cntInput = input("数量（默认 10）", numeric = true)
        cntInput.setText("10")
        addView(cntInput)

        var upper = false
        var strip = false
        addView(switchRow("使用大写字母") { upper = it })
        addView(switchRow("去除横杠 -") { strip = it })

        val out = output("生成的 UUID", minLines = 8)
        val btn = primaryBtn("🔑  生成 UUID") {
            val count = cntInput.text.toString().ifBlank { "10" }.toIntOrNull()
                ?.coerceIn(1, 500) ?: 10
            val list = (1..count).map {
                var u = UUID.randomUUID().toString()
                if (strip) u = u.replace("-", "")
                if (upper) u.uppercase() else u
            }
            out.setText(list.joinToString("\n"))
        }
        addView(row(btn))
        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
