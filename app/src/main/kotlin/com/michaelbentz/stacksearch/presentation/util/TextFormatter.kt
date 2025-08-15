package com.michaelbentz.stacksearch.presentation.util

fun String.htmlToPlainText(): String {
    return this
        .replace(Regex("(?i)<br\\s*/?>"), "\n")
        .replace(Regex("(?i)</p>"), "\n\n")
        .replace(Regex("(?i)<li>"), "• ")
        .replace(Regex("<[^>]*>"), " ")
        .replace(Regex("[ \\t\\x0B\\f\\r]+"), " ")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}

fun String.oneLineExcerpt(maxChars: Int): String {
    return replace(Regex("\\s+"), " ").trim().take(maxChars)
}

fun String.stripHtml(): String {
    return replace(Regex("<[^>]*>"), " ").replace(Regex("\\s+"), " ").trim()
}
