package com.app.wardove.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TimeUtilsTest {

    private val newYork: ZoneId = ZoneId.of("America/New_York")
    private val utc: ZoneId = ZoneId.of("UTC")

    private fun at(zone: ZoneId, y: Int, m: Int, d: Int, h: Int = 12): Long =
        LocalDateTime.of(y, m, d, h, 0).atZone(zone).toInstant().toEpochMilli()

    @Test
    fun boundsCoverAFullUtcDay() {
        val (start, end) = dayBoundsMillis(at(utc, 2026, 3, 15), utc)
        assertEquals(LocalDate.of(2026, 3, 15).atStartOfDay(utc).toInstant().toEpochMilli(), start)
        assertEquals(24L * 60 * 60 * 1000, end - start)
    }

    @Test
    fun springForwardDayIsTwentyThreeHours() {
        // US DST starts 2026-03-08: the local day has only 23 hours.
        val (start, end) = dayBoundsMillis(at(newYork, 2026, 3, 8), newYork)
        assertEquals(23L * 60 * 60 * 1000, end - start)
    }

    @Test
    fun fallBackDayIsTwentyFiveHours() {
        // US DST ends 2026-11-01: the local day has 25 hours.
        val (start, end) = dayBoundsMillis(at(newYork, 2026, 11, 1), newYork)
        assertEquals(25L * 60 * 60 * 1000, end - start)
    }

    @Test
    fun lateEveningWearStillCountsAsSameDay() {
        val morning = at(newYork, 2026, 6, 5, 7)
        val night = at(newYork, 2026, 6, 5, 23)
        assertTrue(isSameLocalDay(morning, night, newYork))
        assertFalse(isSameLocalDay(morning, at(newYork, 2026, 6, 6, 0), newYork))
    }
}
