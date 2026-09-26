package com.mindyhsu.minweather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val cod: String? = null,
    val message: Int? = null,
    @SerialName("cnt") val numTimestamp: Int? = null,
    val list: List<ForecastItem>,
    val city: City? = null
)
