package com.michaelbentz.stacksearch.presentation.util

import androidx.core.text.HtmlCompat

fun String.htmlExcerpt(maxChars: Int = 340): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
        .toString()
        .replace('\u00A0', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()
        .take(maxChars)
}
