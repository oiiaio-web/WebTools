package com.oiiaio.web.tools.tools.text

import com.oiiaio.web.tools.ui.BaseToolActivity
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class XmlActivity : BaseToolActivity() {
    override fun toolTitle() = "XML 格式化 / 校验"

    override fun onBuild() {
        label("输入 XML")
        val inp = input("粘贴 XML 内容", multi = true, mono = true, minLines = 7)
        addView(inp)

        val out = output("格式化结果", minLines = 8)

        fun format(raw: String): String {
            val writer = StringWriter()
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(StreamSource(StringReader(raw)), StreamResult(writer))
            return writer.toString()
        }

        fun validate(raw: String): Boolean {
            DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(org.xml.sax.InputSource(StringReader(raw)))
            return true
        }

        addView(row(
            primaryBtn("格式化") {
                try {
                    out.setText(format(inp.text.toString().trim()))
                } catch (e: Exception) {
                    out.setText("✗ XML 解析失败：${e.message}")
                }
            },
            ghostBtn("校验") {
                try {
                    validate(inp.text.toString().trim())
                    out.setText("✓ XML 格式合法")
                } catch (e: Exception) {
                    out.setText("✗ XML 解析失败：${e.message}")
                }
            }
        ))
        label("结果")
        addView(out)
        addView(resultActions(out))
    }
}
