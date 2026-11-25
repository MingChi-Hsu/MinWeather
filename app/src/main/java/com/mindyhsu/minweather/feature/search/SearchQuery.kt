package com.mindyhsu.minweather.feature.search

const val SEARCH_DEBOUNCE_MS = 500L

private val CJK_SCRIPTS = setOf(
    Character.UnicodeScript.HAN,
    Character.UnicodeScript.HIRAGANA,
    Character.UnicodeScript.KATAKANA,
    Character.UnicodeScript.HANGUL
)

/**
 * Many Chinese, Japanese and Korean place names are only two characters (e.g. 台北),
 * while two Latin letters are too short to mean anything.
 */
fun isLongEnoughToSearch(text: String): Boolean {
    val trimmed = text.trim()
    val hasCjk = trimmed.any { Character.UnicodeScript.of(it.code) in CJK_SCRIPTS }
    return trimmed.length >= if (hasCjk) 2 else 3
}
