package com.sup.dev.java.libs.text_format

import com.sup.dev.java.libs.debug.err
import com.sup.dev.java.tools.ToolsText

class TextFormatter(
        val text: String
) {

    private val char_protector = '\\'
    private val char_protector_word = '@'
    private val char_no_format = "[noFormat]"
    private val char_no_format_end = "[/noFormat]"
    private val chars_spec = arrayOf(char_protector, char_protector_word, '*', '^', '~', '_', '{', '}')

    private val textLow = text.toLowerCase()
    private var result: String? = null
    private var i = 0
    private var skipToSpace = false
    private var skipToNextNoFormat = false

    fun parseHtml(): String {
        if (result == null) parseText()
        return result!!
    }

    fun parseNoTags(): String {
        return parseHtml().replace(Regex("\\<[^>]*>"), "")
    }

    private fun parseText() {
        result = ""
        while (i < text.length) {
            if (skipToSpace) {
                if (text[i] == ' ') {
                    skipToSpace = false
                    if (text[i - 1] != char_protector_word && chars_spec.contains(text[i - 1])) i--
                } else {
                    result += text[i++]
                    continue
                }
            }

            if (skipToNextNoFormat) {
                if (text[i] == '[' && text.length - i >= char_no_format_end.length && text.substring(i, i + char_no_format_end.length) == char_no_format_end
                        && (i == 0 || text[i - 1] != char_protector)) {
                    i += char_no_format_end.length
                    skipToNextNoFormat = false
                    continue
                } else {
                    result += text[i++]
                    continue
                }
            }

            if (text[i] == char_protector
                    && text.length > i + 1
                    && (text[i + 1] != char_protector && chars_spec.contains(text[i + 1]))) {
                i++
                result += text[i++]
                continue
            }

            if (text[i] == '[' && text.length - i >= char_no_format.length && text.substring(i, i + char_no_format.length) == char_no_format
                    && (i == 0 || text[i - 1] != char_protector)) {
                i += char_no_format.length
                skipToNextNoFormat = true
                continue
            }

            if (text[i] == char_protector_word) {
                skipToSpace = true
                result += text[i++]
                continue
            }
            if (parseHtml('*', "<\$b>", "</\$b>")) continue
            if (parseHtml('^', "<\$i>", "</\$i>")) continue
            if (parseHtml('~', "<\$s>", "</\$s>")) continue
            if (parseHtml('_', "<\$u>", "</\$u>")) continue
            if (parseLink()) continue
            if (parseColorHash()) continue
            if (parseColorName("red", "D32F2F")) continue
            if (parseColorName("pink", "C2185B")) continue
            if (parseColorName("purple", "7B1FA2")) continue
            if (parseColorName("indigo", "303F9F")) continue
            if (parseColorName("blue", "1976D2")) continue
            if (parseColorName("cyan", "0097A7")) continue
            if (parseColorName("teal", "00796B")) continue
            if (parseColorName("green", "388E3C")) continue
            if (parseColorName("lime", "689F38")) continue
            if (parseColorName("yellow", "FBC02D")) continue
            if (parseColorName("amber", "FFA000")) continue
            if (parseColorName("orange", "F57C00")) continue
            if (parseColorName("brown", "5D4037")) continue
            if (parseColorName("grey", "616161")) continue
            if (parseColorName("campfire", "FF6D00")) continue
            if (parseColorName("rainbow", "-")) continue
            if (parseColorName("gay", "-")) continue
            if (parseColorName("xmas", "-")) continue
            if (parseColorName("christmas", "-")) continue
            result += text[i++]
        }
    }

    private fun parseHtml(c: Char, open: String, close: String): Boolean {
        if (text[i] == c) {
            val next = findNext(c, 0)
            if (next != -1) {
                result += open + TextFormatter(text.substring(i + 1, next)).parseHtml() + close
                i = next + 1
                return true
            }
        }
        return false
    }

    private fun findNext(c: Char, offset: Int): Int {
        var next = -1
        var skip = false
        var skipToSpace = false
        var n = i + 1 + offset
        while (n < text.length) {
            if (skip) {
                skip = false
                n++
                continue
            }
            if (skipToSpace) {
                if (text[n] == ' ') {
                    skipToSpace = false
                    if (text[n - 1] != char_protector_word && chars_spec.contains(text[n - 1])) {
                        n--
                    }
                } else {
                    n++
                    continue
                }
            }
            if (text[n] == c) {
                next = n
                break
            } else if (text[n] == char_protector) {
                skip = true
            } else if (text[n] == char_protector_word) {
                skipToSpace = true
            }
            n++
        }
        return next
    }

    private fun parseColorName(name: String, hash: String): Boolean {
        try {
            if (text[i] == '{') {
                for (n in name.indices) if (textLow[i + 1 + n] != name[n]) return false

                if (text[i + name.length + 1] == ' ') {
                    val next = findNext('}', name.length + 1)
                    if (next != -1) {
                        if (name == "rainbow") {
                            val t = text.substring(i + name.length + 2, next)
                            var x = -1
                            for (i in t) result += rainbow("$i", x++)
                        } else if (name == "gay") {
                            val t = text.substring(i + name.length + 2, next)
                            var x = -1
                            for (i in t) result += gay("$i", x++)
                        } else if (name == "xmas" || name == "christmas") {
                            val t = text.substring(i + name.length + 2, next)
                            var x = -1
                            for (i in t) result += xmas("$i", x++)
                        } else {
                            val t = TextFormatter(text.substring(i + name.length + 2, next)).parseHtml()
                            result += "<font color=\"#$hash\">$t</font>"
                        }
                        i = next + 1
                        return true
                    }
                }
            }
            return false
        } catch (e: Exception) {
            err(e)
            return false
        }
    }

    private fun rainbow(s: String, index: Int): String {
        if(s.length > 200) return s
        return when ((index + 1) % 7) {
            0 -> "<font color=\"#d5302e\">$s</font>"
            1 -> "<font color=\"#f67c01\">$s</font>"
            2 -> "<font color=\"#f8c129\">$s</font>"
            3 -> "<font color=\"#3c8f3d\">$s</font>"
            4 -> "<font color=\"#1e75d2\">$s</font>"
            5 -> "<font color=\"#014efc\">$s</font>"
            6 -> "<font color=\"#77229b\">$s</font>"
            else -> "<font color=\"#000000\">$s</font>"
        }
    }
    private fun gay(s: String, index: Int): String {
        if(s.length > 200) return s
        return when ((index + 1) % 6) {
            0 -> "<font color=\"#d5302e\">$s</font>"
            1 -> "<font color=\"#f67c01\">$s</font>"
            2 -> "<font color=\"#f8c129\">$s</font>"
            3 -> "<font color=\"#3c8f3d\">$s</font>"
            4 -> "<font color=\"#1e75d2\">$s</font>"
            5 -> "<font color=\"#77229b\">$s</font>"
            else -> "<font color=\"#000000\">$s</font>"
        }
    }

    private fun xmas(s: String, index: Int): String {
        if(s.length > 200) return s
        return when ((index + 1) % 2) {
            0 -> "<font color=\"#D32F2F\">$s</font>"
            1 -> "<font color=\"#D1D1D1\">$s</font>"
            else -> "<font color=\"#000000\">$s</font>"
        }
    }

    private fun parseColorHash(): Boolean {
        try {
            if (text[i] == '{') {
                val c1 = nextColorChar(i + 1)
                val c2 = nextColorChar(i + 2)
                val c3 = nextColorChar(i + 3)
                val c4 = nextColorChar(i + 4)
                val c5 = nextColorChar(i + 5)
                val c6 = nextColorChar(i + 6)
                if (c1 != null && c2 != null && c3 != null && c4 != null && c5 != null && c6 != null && text[i + 7] == ' ') {
                    val color = "" + c1 + c2 + c3 + c4 + c5 + c6
                    val next = findNext('}', 7)
                    if (next != -1) {
                        result += "<font color=\"#$color\">${TextFormatter(
                                text.substring(
                                        i + 8,
                                        next
                                )
                        ).parseHtml()}</font>"
                        i = next + 1
                        return true
                    }
                }
            }
            return false
        } catch (e: Exception) {
            err(e)
            return false
        }
    }

    private fun parseLink(): Boolean {
        try {
            if (text[i] == '[') {
                val nextClose = findNext(']', 0)

                if (nextClose == -1) return false

                var nextSpace = findNext(' ', nextClose - i)
                if (nextSpace == -1) nextSpace = text.length

                if (ToolsText.TEXT_CHARS_s.contains(text[nextSpace - 1])) nextSpace--
                val name = text.substring(i + 1, nextClose)
                val link = text.substring(nextClose + 1, nextSpace)

                if (ToolsText.isWebLink(link) || link.startsWith(char_protector_word)) {
                    result += "<a href=\"${ToolsText.castToWebLink(link)}\">$name</a>"
                    i = nextSpace
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            err(e)
            return false
        }
    }

    private fun nextColorChar(i: Int): Char? {
        if (text[i] == '0' || text[i] == '1' || text[i] == '2' || text[i] == '3' || text[i] == '4' || text[i] == '5' || text[i] == '6' || text[i] == '7' || text[i] == '8' || text[i] == '9' || textLow[i] == 'a' || textLow[i] == 'b' || textLow[i] == 'c' || textLow[i] == 'd' || textLow[i] == 'e' || textLow[i] == 'f') return text[i]
        return null
    }

}