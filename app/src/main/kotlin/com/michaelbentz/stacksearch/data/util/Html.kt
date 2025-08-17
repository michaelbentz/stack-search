package com.michaelbentz.stacksearch.data.util

internal fun String.unescapeHtml(): String {
    var string = this
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&nbsp;", " ")
    string = string.replace(Regex("&#(\\d+);")) { m ->
        m.groupValues[1].toInt().toChar().toString()
    }
    string = string.replace(Regex("&#x([0-9A-Fa-f]+);")) { m ->
        m.groupValues[1].toInt(16).toChar().toString()
    }
    return string
}
