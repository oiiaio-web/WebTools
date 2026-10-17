package com.oiiaio.web.tools.tools.web

import com.oiiaio.web.tools.ui.BaseToolActivity
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class JsonToKotlinActivity : BaseToolActivity() {
    override fun toolTitle() = "JSON 转 Kotlin 数据类"

    private val classes = LinkedHashMap<String, String>()
    private val usedNames = mutableSetOf<String>()

    private fun pascal(s: String): String =
        s.split(Regex("[^a-zA-Z0-9]")).filter { it.isNotEmpty() }
            .joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
            .ifEmpty { "Item" }

    private fun unique(name: String): String {
        if (usedNames.add(name)) return name
        var i = 2
        while (!usedNames.add("$name$i")) i++
        return "$name$i"
    }

    private fun singularize(name: String): String =
        if (name.endsWith("s", true) && name.length > 1) name.dropLast(1) else "${name}Item"

    private fun safeKey(key: String): String =
        if (key.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) key else "`$key`"

    private fun inferType(v: Any?, proposed: String): String = when (v) {
        is JSONObject -> {
            val cn = unique(proposed)
            genObject(v, cn)
            cn
        }
        is JSONArray -> {
            val first = (0 until v.length()).map { v.get(it) }
                .firstOrNull { it != null && it != JSONObject.NULL }
            if (first == null) "List<Any?>"
            else "List<${inferType(first, singularize(proposed))}>"
        }
        is Boolean -> "Boolean"
        is Double, is Float -> "Double"
        is Number -> "Long"
        is String -> "String"
        else -> "Any?"
    }

    private fun genObject(obj: JSONObject, className: String) {
        val fields = obj.keys().asSequence().map { key ->
            val v = obj.get(key)
            val type = if (v == null || v == JSONObject.NULL) "Any?"
            else inferType(v, pascal(key))
            "  val ${safeKey(key)}: $type? = null"
        }.toList()
        classes[className] = "data class $className(\n" + fields.joinToString(",\n") + "\n)"
    }

    private fun generate(raw: String, rootName: String): String {
        classes.clear()
        usedNames.clear()
        val parsed = JSONTokener(raw.trim()).nextValue()
        require(parsed is JSONObject) { "顶层需要是 JSON 对象（{ ... }）" }
        val root = unique(pascal(rootName.ifBlank { "Response" }))
        genObject(parsed, root)
        return classes.values.joinToString("\n\n")
    }

    override fun onBuild() {
        label("根类名")
        val nameInput = input("Response", mono = true)
        nameInput.setText("Response")
        addView(nameInput)

        label("JSON 内容")
        val jsonInput = input("粘贴 JSON 对象", multi = true, mono = true, minLines = 6)
        addView(jsonInput)

        val out = output("生成的 Kotlin data class", minLines = 9)
        addView(row(primaryBtn("生成 Kotlin 代码") {
            try {
                out.setText(generate(jsonInput.text.toString(), nameInput.text.toString()))
            } catch (e: Exception) {
                out.setText("✗ 生成失败：${e.message}")
            }
        }))
        label("结果（字段默认可空，可按需调整）")
        addView(out)
        addView(resultActions(out))
    }
}
