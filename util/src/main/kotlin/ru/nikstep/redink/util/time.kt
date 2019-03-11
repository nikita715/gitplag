package ru.nikstep.redink.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * [Date] to iso representation
 */
fun Date.asIsoString(): String {
    val tz = TimeZone.getTimeZone("UTC")
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    df.timeZone = tz
    return df.format(time)
}
