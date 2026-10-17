package com.oiiaio.web.tools.tools.encode

import com.oiiaio.web.tools.ui.CodecActivity

class MorseActivity : CodecActivity() {
    override fun toolTitle() = "摩斯电码编解码"
    override fun monoInput() = false

    private val table = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
        'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
        'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
        'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
        'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
        'Z' to "--..",
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--",
        '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----.",
        '.' to ".-.-.-", ',' to "--..--", '?' to "..--..", '!' to "-.-.--",
        '/' to "-..-.", '(' to "-.--.", ')' to "-.--.-", '&' to ".-...",
        ':' to "---...", ';' to "-.-.-.", '=' to "-...-", '+' to ".-.-.",
        '-' to "-....-", '_' to "..--.-", '"' to ".-..-.", '@' to ".--.-."
    )

    override fun encode(s: String): String =
        s.uppercase().split(" ").joinToString(" / ") { word ->
            word.mapNotNull { table[it] }.joinToString(" ")
        }

    override fun decode(s: String): String {
        val rev = table.entries.associate { it.value to it.key }
        return s.trim().split("/").joinToString(" ") { word ->
            word.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                .map { code -> rev[code]?.toString() ?: "?" }
                .joinToString("")
        }.trim()
    }
}
