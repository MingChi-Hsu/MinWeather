package com.mindyhsu.minweather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather> = emptyList(),
    val clouds: Clouds? = null,
    val wind: Wind? = null,
    val visibility: Int? = null,
    val pop: Double? = null, // probability of precipitation
    val rain: RainAndSnow3H? = null,
    val sys: Pod? = null, // Part of the Day
    @SerialName("dt_txt") val dtTxt: String? = null
)
