package com.oiiaio.web.tools.tools.encode

import com.oiiaio.web.tools.ui.CodecActivity

class HtmlEntityActivity : CodecActivity() {
    override fun toolTitle() = "HTML 实体编解码"

    private val named = linkedMapOf(
        "&" to "&amp;",
        "<" to "&lt;",
        ">" to "&gt;",
        "\"" to "&quot;",
        "'" to "&#39;",
        " " to "&nbsp;",
        "©" to "&copy;",
        "®" to "&reg;",
        "™" to "&trade;",
        "…" to "&hellip;",
        "—" to "&mdash;",
        "–" to "&ndash;",
        "°" to "&deg;",
        "·" to "&middot;",
        "«" to "&laquo;",
        "»" to "&raquo;",
        "±" to "&plusmn;",
        "×" to "&times;",
        "÷" to "&divide;",
        "€" to "&euro;",
        "£" to "&pound;",
        "¥" to "&yen;"
    )
    private val reverse = named.entries.associate { it.value to it.key }

    private val entityRegex = Regex("&(?:#(\\d+)|#x([0-9a-fA-F]+)|([a-zA-Z]+));")

    override fun encode(s: String): String = buildString {
        s.forEach { c ->
            val entity = named.entries.firstOrNull { it.key == c.toString() }?.value
            when {
                entity != null -> append(entity)
                c.code > 127 -> append("&#x%x;".format(c.code))
                else -> append(c)
            }
        }
    }

    override fun decode(s: String): String = entityRegex.replace(s) { m ->
        when {
            m.groupValues[1].isNotEmpty() ->
                String(Character.toChars(m.groupValues[1].toInt()))
            m.groupValues[2].isNotEmpty() ->
                String(Character.toChars(m.groupValues[2].toInt(16)))
            else -> reverse["&${m.groupValues[3]};"] ?: m.value
        }
    }
}
