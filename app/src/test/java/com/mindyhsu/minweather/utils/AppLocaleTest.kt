package com.mindyhsu.minweather.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class AppLocaleTest {

    @Test
    fun `Traditional Chinese regions and script use Traditional Chinese weather descriptions`() {
        assertEquals("zh_tw", Locale.TRADITIONAL_CHINESE.toOpenWeatherLanguage())
        assertEquals("zh_tw", Locale.forLanguageTag("zh-HK").toOpenWeatherLanguage())
        assertEquals("zh_tw", Locale.forLanguageTag("zh-Hant-GB").toOpenWeatherLanguage())
    }

    @Test
    fun `other languages use English weather descriptions`() {
        assertEquals("en", Locale.UK.toOpenWeatherLanguage())
        assertEquals("en", Locale.FRANCE.toOpenWeatherLanguage())
        assertEquals("en", Locale.SIMPLIFIED_CHINESE.toOpenWeatherLanguage())
    }
}
