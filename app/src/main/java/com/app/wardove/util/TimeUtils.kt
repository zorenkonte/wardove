package com.app.wardove.util

import java.time.Instant
import java.time.ZoneId

/**
 * Start (inclusive) and end (exclusive) epoch-millis of the local calendar day
 * containing [epochMillis] in [zone]. Uses java.time so days that span a DST
 * change are 23 or 25 hours long instead of a hard-coded 24.
 */
fun dayBoundsMillis(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> {
    val date = Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()
    val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
    val end = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
    return start to end
}

/** True when both instants fall on the same local calendar day in [zone]. */
fun isSameLocalDay(a: Long, b: Long, zone: ZoneId = ZoneId.systemDefault()): Boolean =
    Instant.ofEpochMilli(a).atZone(zone).toLocalDate() ==
        Instant.ofEpochMilli(b).atZone(zone).toLocalDate()
