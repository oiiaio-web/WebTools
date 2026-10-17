package com.oiiaio.web.tools.ui

import android.widget.EditText
import android.widget.LinearLayout

/**
 * Template for the many "text -> encode / decode -> text" tools.
 * Subclasses only implement [encode] / [decode] and the title.
 */
abstract class CodecActivity : BaseToolActivity() {

    protected var mode = 0
    protected lateinit var output: EditText
        private set

    protected open val encodeLabel get() = "编码 / Encrypt"
    protected open val decodeLabel get() = "解码 / Decrypt"
    protected open fun inputHint() = "请输入要处理的文本"
    protected open fun monoInput() = true

    @Throws(Exception::class)
    protected open fun encode(s: String): String = throw UnsupportedOperationException("未实现编码")

    @Throws(Exception::class)
    protected open fun decode(s: String): String = throw UnsupportedOperationException("未实现解码")

    /** Hook for subclasses to add switches / options below the input. */
    protected open fun extra(container: LinearLayout) {}

    override fun onBuild() {
        label("输入内容")
        val inp = input(inputHint(), multi = true, mono = monoInput(), minLines = 5)
        val optionHost = LinearLayout(this)
        optionHost.orientation = LinearLayout.VERTICAL
        extra(optionHost)
        addView(optionHost)

        val seg = segmented(listOf(encodeLabel, decodeLabel)) { idx, _ -> mode = idx }
        addView(seg)

        val run = primaryBtn("▶  执行") {
            val src = inp.text.toString()
            if (src.isEmpty()) {
                toast("请先输入内容")
                return@primaryBtn
            }
            try {
                val r = if (mode == 0) encode(src) else decode(src)
                output.setText(r)
            } catch (t: Throwable) {
                output.setText("⚠️ 处理失败：${t.message ?: t.javaClass.simpleName}")
                toast("处理失败，请检查输入")
            }
        }
        addView(row(run))

        label("输出结果")
        output = output("结果输出", minLines = 5)
        addView(output)
        addView(resultActions(output))
    }
}
