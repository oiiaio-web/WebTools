package com.oiiaio.web.tools.tools.text

import com.oiiaio.web.tools.ui.BaseToolActivity

class TextTransformActivity : BaseToolActivity() {
    override fun toolTitle() = "文本批量处理"

    override fun onBuild() {
        label("输入文本")
        val inp = input("输入要处理的文本", multi = true, mono = false, minLines = 5)
        addView(inp)

        val out = output("处理结果", minLines = 5)

        fun onText(transform: (String) -> String) {
            out.setText(transform(inp.text.toString()))
        }
        fun onLines(transform: (List<String>) -> List<String>) {
            out.setText(transform(inp.text.toString().lines()).joinToString("\n"))
        }

        addView(row(
            ghostBtn("全大写") { onText { it.uppercase() } },
            ghostBtn("全小写") { onText { it.lowercase() } },
            ghostBtn("词首大写") {
                onText { s ->
                    s.split(" ").joinToString(" ") { w ->
                        w.lowercase().replaceFirstChar { c -> c.uppercase() }
                    }
                }
            }
        ))
        addView(row(
            ghostBtn("反转全文") { onText { it.reversed() } },
            ghostBtn("去所有空白") { onText { it.replace(Regex("\\s"), "") } },
            ghostBtn("行首尾去空") { onLines { ls -> ls.map { it.trim() } } }
        ))
        addView(row(
            ghostBtn("行排序") { onLines { it.sorted() } },
            ghostBtn("行去重") { onLines { it.distinct() } },
            ghostBtn("去除空行") { onLines { it.filter { l -> l.isNotBlank() } } }
        ))
        addView(row(
            ghostBtn("行倒序") { onLines { it.reversed() } },
            ghostBtn("数字行排序") {
                onLines { ls ->
                    ls.sortedBy { it.trim().toDoubleOrNull() ?: Double.MAX_VALUE }
                }
            },
            ghostBtn("中英加空格") {
                onText { s ->
                    s.replace(Regex("([\\u4e00-\\u9fa5])([A-Za-z0-9])"), "$1 $2")
                        .replace(Regex("([A-Za-z0-9])([\\u4e00-\\u9fa5])"), "$1 $2")
                }
            }
        ))

        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
