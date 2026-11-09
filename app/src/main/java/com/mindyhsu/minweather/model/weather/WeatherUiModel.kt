package com.mindyhsu.minweather.model.weather

import com.mindyhsu.minweather.common.DateTimeConstants.DATE_FORMAT
import com.mindyhsu.minweather.common.DateTimeConstants.DAY_OF_WEEK_FORMAT
import com.mindyhsu.minweather.common.DateTimeConstants.TIME_FORMAT_AM_PM
import com.mindyhsu.minweather.utils.toUrl
import com.mindyhsu.minweather.utils.toCelsiusString
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class WeatherUiModel(
    val currentWeather: CurrentWeather,
    val forecast: Forecast,
    // Name of the place the user picked in search. The weather API's own `name` is the nearest
    // OpenWeather city to the coordinates, which can differ from the place the user chose
    val selectedLocationName: String? = null
) {
    private val zoneOffset: ZoneOffset by lazy { ZoneOffset.ofTotalSeconds(currentWeather.timezone) }

    private fun epochToDateTime(second: Long): LocalDateTime =
        Instant.ofEpochSecond(second).atOffset(zoneOffset).toLocalDateTime()

    fun getNow() = epochToDateTime(currentWeather.dt)

    fun getLocationName(): String =
        selectedLocationName ?: listOfNotNull(currentWeather.name, currentWeather.sys?.country)
            .filter { it.isNotBlank() }
            .joinToString(", ")

    fun getSunriseDateTime() = currentWeather.sys?.sunrise?.let { epochToDateTime(it) }

    fun getSunsetDateTime() = currentWeather.sys?.sunset?.let { epochToDateTime(it) }

    fun getIconUrl() = currentWeather.weather.firstOrNull()?.icon?.toUrl().orEmpty()

    fun getNext24HForecast(): List<Forecast24HUiModel> {
        val now = getNow()
        val end = now.plusDays(1)

        return forecast.list
            .map {
                it to epochToDateTime(it.dt)
            }.takeWhile { (_, dt) ->
                dt.isBefore(end)
            }.map { (item, dt) ->
                Forecast24HUiModel(
                    timeStr = dt.format(DateTimeFormatter.ofPattern(TIME_FORMAT_AM_PM)),
                    icon = item.weather.firstOrNull()?.icon?.toUrl().orEmpty(),
                    tempStr = item.main.temp.toCelsiusString()
                )
            }
    }

    fun getNext5DForecast(): List<Forecast5DUiModel> {
        val today = getNow().toLocalDate()
        return forecast.list
            .map {
                it to epochToDateTime(it.dt)
            }.filter { (_, dt) ->
                dt.toLocalDate().isAfter(today)
            }.groupBy { (_, dt) ->
                dt.toLocalDate()
            }.map { (_, items) ->
                // The 3-hour slot closest to midday represents the day's weather (icon and temperature)
                val (midday, middayDt) = items.minBy { (_, dt) -> abs(dt.hour - MIDDAY_HOUR) }

                // temp_min / temp_max of a single slot only describe that 3-hour slot,
                // so the daily high and low come from all slots of the day
                Forecast5DUiModel(
                    dateStr = middayDt.format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                    dayOfWeek = middayDt.format(DateTimeFormatter.ofPattern(DAY_OF_WEEK_FORMAT)),
                    icon = midday.weather.firstOrNull()?.icon?.toUrl().orEmpty(),
                    tempStr = midday.main.temp.toCelsiusString(),
                    minTempStr = items.minOf { (item, _) -> item.main.tempMin }.toCelsiusString(),
                    maxTempStr = items.maxOf { (item, _) -> item.main.tempMax }.toCelsiusString()
                )
            }
    }

    private companion object {
        const val MIDDAY_HOUR = 12
    }
}
