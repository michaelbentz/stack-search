package com.michaelbentz.stacksearch.presentation.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

fun Int.formatThousands(): String = "%,d".format(this)

fun Int.formatCompact(locale: Locale = Locale.getDefault()): String {
    return when {
        this >= 1_000_000 -> "${format1DecOrInt(this / 1_000_000.0, locale)}m"
        this >= 1_000 -> "${format1DecOrInt(this / 1_000.0, locale)}k"
        else -> NumberFormat.getIntegerInstance(locale).format(this)
    }
}

fun format1DecOrInt(value: Double, locale: Locale): String {
    val scaled = (value * 10).roundToInt()
    val isInt = scaled % 10 == 0
    return if (isInt) {
        String.format(locale, "%d", scaled / 10)
    } else {
        String.format(locale, "%.1f", scaled / 10.0)
    }
}
