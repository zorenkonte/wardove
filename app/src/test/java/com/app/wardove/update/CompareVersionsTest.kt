package com.app.wardove.update

import com.app.wardove.ui.update.compareVersions
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class CompareVersionsTest {

    @Test
    fun newerPatchIsGreater() {
        assertTrue(compareVersions("2.0.24", "2.0.0") > 0)
    }

    @Test
    fun newerMajorBeatsOlderHigherPatch() {
        assertTrue(compareVersions("2.0.0", "1.0.23") > 0)
    }

    @Test
    fun equalVersionsReturnZero() {
        assertEquals(0, compareVersions("2.0.5", "2.0.5"))
    }

    @Test
    fun leadingVAndDifferingLengthsTolerated() {
        assertEquals(0, compareVersions("v2.0", "2.0.0"))
        assertTrue(compareVersions("v2.0.1", "2.0") > 0)
    }

    @Test
    fun olderVersionIsLess() {
        assertTrue(compareVersions("1.0.23", "2.0.0") < 0)
    }
}
