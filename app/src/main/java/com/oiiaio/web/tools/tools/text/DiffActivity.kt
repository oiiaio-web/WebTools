package com.oiiaio.web.tools.tools.text

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.oiiaio.web.tools.ui.BaseToolActivity

class DiffActivity : BaseToolActivity() {
    override fun toolTitle() = "文本对比（Diff）"

    private data class DLine(val type: Int, val text: String) // 0 same, 1 removed, 2 added

    private fun lineDiff(a: List<String>, b: List<String>): List<DLine> {
        val m = a.size
        val n = b.size
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in m - 1 downTo 0)
            for (j in n - 1 downTo 0)
                dp[i][j] = if (a[i] == b[j]) dp[i + 1][j + 1] + 1
                else maxOf(dp[i + 1][j], dp[i][j + 1])
        val out = ArrayList<DLine>()
        var i = 0
        var j = 0
        while (i < m && j < n) {
            when {
                a[i] == b[j] -> { out.add(DLine(0, a[i])); i++; j++ }
                dp[i + 1][j] >= dp[i][j + 1] -> { out.add(DLine(1, a[i])); i++ }
                else -> { out.add(DLine(2, b[j])); j++ }
            }
        }
        while (i < m) { out.add(DLine(1, a[i])); i++ }
        while (j < n) { out.add(DLine(2, b[j])); j++ }
        return out
    }

    override fun onBuild() {
        label("文本 A（原文本）")
        val a = input("粘贴第一段文本", multi = true, mono = true, minLines = 4)
        addView(a)
        label("文本 B（新文本）")
        val b = input("粘贴第二段文本", multi = true, mono = true, minLines = 4)
        addView(b)

        val summary = TextView(this).apply { setTextColor(cSub); textSize = 13f }
        val host = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        addView(row(primaryBtn("🔍  开始对比") {
            val result = lineDiff(a.text.toString().lines(), b.text.toString().lines())
            host.removeAllViews()
            val removed = result.count { it.type == 1 }
            val added = result.count { it.type == 2 }
            val same = result.count { it.type == 0 }
            summary.text = "相同 $same 行 · 删除 $removed 行 · 新增 $added 行"
            result.forEach { line ->
                val (fg, bg, pre) = when (line.type) {
                    1 -> Triple(0xFFE0667A.toInt(), 0x14E0667A, "−  ")
                    2 -> Triple(0xFF36B6A8.toInt(), 0x1436B6A8, "+  ")
                    else -> Triple(cSub, 0, "    ")
                }
                val tv = TextView(this@DiffActivity).apply {
                    text = pre + line.text
                    setTextColor(fg)
                    setBackgroundColor(bg)
                    textSize = 12.5f
                    typeface = Typeface.MONOSPACE
                    setPadding(dp(8f), dp(5f), dp(8f), dp(5f))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                host.addView(tv)
            }
        }))

        label("对比结果")
        card {
            addView(summary)
            addView(host)
        }
    }
}
