package com.mindyhsu.minweather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val coord: Coordinator? = null,
    val weather: List<Weather> = emptyList(),
    val base: String? = null,
    val main: Main,
    val visibility: Int? = null,
    val wind: Wind? = null,
    val rain: RainAndSnow? = null,
    val snow: RainAndSnow? = null,
    val clouds: Clouds? = null,
    val dt: Long,
    val sys: Sys? = null,
    val timezone: Int,
    val id: Int? = null,
    val name: String? = null,
    val cod: Int? = null
)
