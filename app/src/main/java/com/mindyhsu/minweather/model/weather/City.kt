package com.mindyhsu.minweather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int? = null,
    val name: String? = null,
    val coord: Coordinator? = null,
    val country: String? = null,
    val population: Int? = null,
    val timezone: Int? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)
