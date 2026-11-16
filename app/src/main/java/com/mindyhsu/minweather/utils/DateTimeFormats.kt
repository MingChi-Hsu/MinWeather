package com.mindyhsu.minweather.utils

import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Date and time formats for the weather screen follow the device locale.
 */
object DateTimeFormats {

    /** Date and time, e.g. "13 Nov 2026, 17:17" (en-GB) or "2026年11月13日 下午5:17" (zh-TW) */
    fun dateTime(locale: Locale): DateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(locale)

    /** Time, e.g. "17:17" (en-GB), "5:17 PM" (en-US) or "下午5:17" (zh-TW) */
    fun time(locale: Locale): DateTimeFormatter =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)

    /** Day and month as numbers, e.g. "13/11" (UK, Hong Kong) or "11/13" (US, Taiwan) */
    fun shortDate(locale: Locale): DateTimeFormatter {
        val pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale
        )
        return DateTimeFormatter.ofPattern(pattern.withoutYear(), locale)
    }

    /** Short day of the week, e.g. "Fri" (en) or "週五" (zh-TW) */
    fun dayOfWeek(locale: Locale): DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE", locale)
}

/** Removes the year and the separator next to it, e.g. "d/M/y" -> "d/M", "y/M/d" -> "M/d", "dd.MM.yy" -> "dd.MM" */
internal fun String.withoutYear(): String =
    replace(Regex("\\W*y+"), "").replace(Regex("^\\W+"), "")
