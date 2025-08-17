package com.michaelbentz.stacksearch.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.format(formatter: DateTimeFormatter): String {
    return Instant
        .ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
