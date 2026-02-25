package com.sfedu.degree_android.core.util

private val spaces = Regex("\\s+")
private val timeRe = Regex("""^(\d{2}):(\d{2})(?::(\d{2}))?$""")

fun formatWorktime(raw: String?): String? {
    val w = raw?.trim()?.replace(spaces, " ")?.takeIf { it.isNotBlank() } ?: return null

    val parts = w.split(" ")
    if (parts.size < 2) return w

    val start = normalizeTime(parts[0]) ?: return w
    val end = normalizeTime(parts[1]) ?: return w

    if (start == "00:00" && end == "00:00") return "Круглосуточно"

    return "с $start до $end"
}

private fun normalizeTime(t: String): String? {
    val m = timeRe.matchEntire(t) ?: return null
    val hh = m.groupValues[1]
    val mm = m.groupValues[2]
    return "$hh:$mm"
}