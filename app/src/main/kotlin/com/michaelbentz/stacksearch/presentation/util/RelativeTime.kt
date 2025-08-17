package com.michaelbentz.stacksearch.presentation.util

import java.time.Duration
import java.time.Instant
import java.time.Period
import java.time.ZoneId

fun Long.toTimeAgo(
    now: Instant = Instant.now(),
    zone: ZoneId = ZoneId.systemDefault(),
    maxParts: Int = 2,
): String {
    val thenZoned = Instant.ofEpochSecond(this).atZone(zone)
    val nowZoned = now.atZone(zone)
    if (!thenZoned.isBefore(nowZoned)) {
        return "just now"
    }
    val parts = mutableListOf<String>()
    val period = Period.between(thenZoned.toLocalDate(), nowZoned.toLocalDate())
    val years = period.years
    val months = period.months
    val days = period.days

    if (years > 0) {
        parts += plural(years.toLong(), "year")
    }
    if (months > 0 && parts.size < maxParts) {
        parts += plural(months.toLong(), "month")
    }
    if (parts.isEmpty() && days > 0) {
        parts += plural(days.toLong(), "day")
    }
    if (parts.isNotEmpty()) {
        return parts.take(maxParts).joinToString(", ") + " ago"
    }
    val duration = Duration.between(thenZoned, nowZoned)
    val minutes = duration.toMinutes() % 60
    val hours = duration.toHours()

    if (hours >= 1) {
        parts += plural(hours, "hour")
        if (minutes > 0 && parts.size < maxParts) {
            parts += plural(minutes, "minute")
        }
        return parts.joinToString(", ") + " ago"
    }
    if (duration.toMinutes() >= 1) {
        return plural(duration.toMinutes(), "minute") + " ago"
    }
    return "just now"
}

private fun plural(n: Long, unit: String): String {
    return "$n $unit" + if (n == 1L) "" else "s"
}
