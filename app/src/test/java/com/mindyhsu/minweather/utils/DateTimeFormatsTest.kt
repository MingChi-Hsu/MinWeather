package com.mindyhsu.minweather.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.util.Locale

class DateTimeFormatsTest {

    // Friday 13 November 2026, 14:00
    private val afternoon = LocalDateTime.of(2026, 11, 13, 14, 0)

    @Test
    fun `withoutYear removes the year wherever it is`() {
        assertEquals("dd/MM", "dd/MM/y".withoutYear())
        assertEquals("M/d", "M/d/yy".withoutYear())
        assertEquals("M/d", "y/M/d".withoutYear())
        assertEquals("dd.MM", "dd.MM.yy".withoutYear())
    }

    @Test
    fun `short date follows the region's order`() {
        // Hong Kong writes the day first, unlike Taiwan
        assertEquals("11/13", afternoon.format(DateTimeFormats.shortDate(Locale.TRADITIONAL_CHINESE)))
        assertEquals("13/11", afternoon.format(DateTimeFormats.shortDate(Locale.forLanguageTag("zh-HK"))))
    }
}
