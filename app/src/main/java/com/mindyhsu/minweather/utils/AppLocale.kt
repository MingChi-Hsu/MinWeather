package com.mindyhsu.minweather.utils

import java.util.Locale

// Taiwan, Hong Kong and Macau
private val TRADITIONAL_CHINESE_REGIONS = setOf("TW", "HK", "MO")

/**
 * Language code for the OpenWeather `lang` parameter, matching the app's languages:
 * Traditional Chinese, or English for everything else.
 */
fun Locale.toOpenWeatherLanguage(): String {
    // "Hant" is the script code for Traditional Chinese (e.g. zh-Hant-TW)
    val isTraditionalChinese =
        language == "zh" && (script == "Hant" || country in TRADITIONAL_CHINESE_REGIONS)
    return if (isTraditionalChinese) "zh_tw" else "en"
}
