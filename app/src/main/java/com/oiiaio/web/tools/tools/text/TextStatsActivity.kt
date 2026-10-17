package com.oiiaio.web.tools.tools.text

import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity

class TextStatsActivity : BaseToolActivity() {
    override fun toolTitle() = "文本统计分析"

    override fun onBuild() {
        label("输入文本")
        val inp = input("粘贴或输入要统计的文本", multi = true, mono = false, minLines = 6)
        addView(inp)

        val host = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val calc = primaryBtn("开始统计") {
            val s = inp.text.toString()
            val lines = s.split("\n")
            val chinese = s.count { it.code in 0x4E00..0x9FFF }
            val english = s.count { it in 'a'..'z' || it in 'A'..'Z' }
            val digits = s.count { it in '0'..'9' }
            val spaces = s.count { it == ' ' }
            val punctuation = s.count { !it.isLetterOrDigit() && !it.isWhitespace() }
            val words = s.split(Regex("\\s+")).count { it.isNotEmpty() }
            val paragraphs = s.split(Regex("\n\\s*\n")).count { it.isNotBlank() }

            host.removeAllViews()
            listOf(
                "字符总数（含空白）" to s.length.toString(),
                "字符总数（不含空白）" to s.count { !it.isWhitespace() }.toString(),
                "字节数（UTF-8）" to s.toByteArray(Charsets.UTF_8).size.toString(),
                "总行数" to lines.size.toString(),
                "非空行数" to lines.count { it.isNotBlank() }.toString(),
                "段落数" to paragraphs.toString(),
                "单词数（空格分隔）" to words.toString(),
                "中文字符" to chinese.toString(),
                "英文字母" to english.toString(),
                "数字字符" to digits.toString(),
                "空格数" to spaces.toString(),
                "标点/符号" to punctuation.toString()
            ).forEach { (k, v) -> host.addView(infoRow(k, v, false)) }
        }
        addView(row(calc))
        label("统计结果")
        card { addView(host) }
    }
}
