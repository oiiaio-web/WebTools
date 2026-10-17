package com.oiiaio.web.tools.tools.text

import com.oiiaio.web.tools.ui.BaseToolActivity

class RegexActivity : BaseToolActivity() {
    override fun toolTitle() = "正则表达式测试"

    override fun onBuild() {
        label("正则表达式")
        val pattern = input("输入正则，例如 \\d{3,4}", mono = true)
        addView(pattern)

        var ignoreCase = false
        var multiLine = false
        var dotAll = false
        addView(switchRow("忽略大小写（i）", false) { ignoreCase = it })
        addView(switchRow("多行模式（m）", false) { multiLine = it })
        addView(switchRow("点号匹配换行（s）", false) { dotAll = it })

        label("测试文本")
        val text = input("输入要测试的文本", multi = true, mono = true, minLines = 4)
        addView(text)

        val out = output("匹配结果", minLines = 6)
        addView(row(primaryBtn("测试匹配") {
            val p = pattern.text.toString()
            if (p.isEmpty()) {
                toast("请输入正则表达式")
                return@primaryBtn
            }
            try {
                val options = buildSet {
                    if (ignoreCase) add(RegexOption.IGNORE_CASE)
                    if (multiLine) add(RegexOption.MULTILINE)
                    if (dotAll) add(RegexOption.DOT_MATCHES_ALL)
                }
                val results = Regex(p, options).findAll(text.text.toString()).toList()
                if (results.isEmpty()) {
                    out.setText("未找到匹配")
                } else {
                    out.setText(buildString {
                        append("共找到 ${results.size} 个匹配\n")
                        append("────────────\n")
                        results.forEachIndexed { i, m ->
                            append("${i + 1}. [${m.range.first}-${m.range.last}]  ${m.value}\n")
                            m.groupValues.forEachIndexed { gi, gv ->
                                if (gi > 0) append("       分组$gi: $gv\n")
                            }
                        }
                    }.trimEnd())
                }
            } catch (e: Exception) {
                out.setText("✗ 正则表达式有误：${e.message}")
            }
        }))

        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
