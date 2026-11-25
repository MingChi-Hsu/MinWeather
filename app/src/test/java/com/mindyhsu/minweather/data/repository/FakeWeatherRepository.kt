package com.mindyhsu.minweather.data.repository

import com.mindyhsu.minweather.model.Result
import com.mindyhsu.minweather.model.weather.CurrentWeather
import com.mindyhsu.minweather.model.weather.Forecast
import com.mindyhsu.minweather.model.weather.Main

class FakeWeatherRepository : WeatherRepository {

    var currentWeatherResult: Result<CurrentWeather> = Result.Success(currentWeather())
    var forecastResult: Result<Forecast> = Result.Success(Forecast(list = emptyList()))

    var currentWeatherCalls = 0
        private set

    override suspend fun getCurrentWeather(latitude: String, longitude: String): Result<CurrentWeather> {
        currentWeatherCalls++
        return currentWeatherResult
    }

    override suspend fun get5DaysForecast(latitude: String, longitude: String): Result<Forecast> =
        forecastResult

    companion object {
        fun currentWeather(name: String = "London") = CurrentWeather(
            main = Main(temp = 283.15, feelsLike = 283.15, tempMin = 283.15, tempMax = 283.15),
            dt = 0,
            timezone = 0,
            name = name
        )
    }
}
