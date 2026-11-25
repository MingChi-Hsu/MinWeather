package com.mindyhsu.minweather.model.weather

import com.mindyhsu.minweather.common.TemperatureConstants.TEMP_K_OFFSET
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Locale

/**
 * Characterisation tests: they record how WeatherUiModel behaves today,
 * so the Clean Architecture refactor can be checked against them.
 */
class WeatherUiModelTest {

    private val utc = ZoneOffset.UTC
    private val taipei = ZoneOffset.ofHours(8)

    // Friday 13 November 2026, 09:00 local time
    private val now = LocalDateTime.of(2026, 11, 13, 9, 0)

    // --- getLocationName() ---

    @Test
    fun `location name is the place picked in search when there is one`() {
        val model = weatherUiModel(name = "Song", country = "TW", selectedLocationName = "Zhongli District, TW")

        assertEquals("Zhongli District, TW", model.getLocationName())
    }

    @Test
    fun `location name falls back to the API city and country`() {
        val model = weatherUiModel(name = "London", country = "GB")

        assertEquals("London, GB", model.getLocationName())
    }

    @Test
    fun `location name skips a blank city name`() {
        val model = weatherUiModel(name = "", country = "GB")

        assertEquals("GB", model.getLocationName())
    }

    @Test
    fun `location name is empty when the API has no city or country`() {
        val model = weatherUiModel(name = null, country = null)

        assertEquals("", model.getLocationName())
    }

    // --- getNext24HForecast() ---

    @Test
    fun `24-hour forecast usually has 8 slots`() {
        // At 09:20 the API's next slot is 12:00, and 09:00 tomorrow is still within 24 hours
        val model = weatherUiModel(
            now = LocalDateTime.of(2026, 11, 13, 9, 20),
            forecastItems = slots(now.toLocalDate(), 12, 15, 18, 21) +
                    slots(now.toLocalDate().plusDays(1), 0, 3, 6, 9, 12)
        )

        val hours = model.getNext24HForecast(Locale.UK)

        assertEquals(8, hours.size)
        assertEquals("12:00", hours.first().timeStr)
        assertEquals("09:00", hours.last().timeStr)
    }

    @Test
    fun `24-hour forecast leaves out a slot exactly 24 hours later`() {
        // At exactly 09:00, 09:00 tomorrow is not before the 24-hour end, so it is left out
        val model = weatherUiModel(
            forecastItems = slots(now.toLocalDate(), 12, 15, 18, 21) +
                    slots(now.toLocalDate().plusDays(1), 0, 3, 6, 9, 12)
        )

        val hours = model.getNext24HForecast(Locale.UK)

        assertEquals(7, hours.size)
        assertEquals("06:00", hours.last().timeStr)
    }

    // --- getNext5DForecast() ---

    @Test
    fun `5-day forecast leaves out today and has one row per day`() {
        val model = weatherUiModel(
            forecastItems = slots(now.toLocalDate(), 12, 15, 18, 21) +
                slots(now.toLocalDate().plusDays(1), 0, 3, 6, 9, 12, 15, 18, 21) +
                slots(now.toLocalDate().plusDays(2), 0, 3, 6, 9, 12, 15, 18, 21)
        )

        val days = model.getNext5DForecast(Locale.UK)

        assertEquals(listOf("14/11", "15/11"), days.map { it.dateStr })
    }

    @Test
    fun `5-day forecast takes the daily high and low from all slots of the day`() {
        val tomorrow = now.toLocalDate().plusDays(1)
        val model = weatherUiModel(
            forecastItems = listOf(
                item(tomorrow.atTime(3, 0), temp = 6.0, min = 5.0, max = 7.0),
                item(tomorrow.atTime(12, 0), temp = 12.0, min = 11.0, max = 13.0),
                item(tomorrow.atTime(15, 0), temp = 14.0, min = 13.0, max = 16.0)
            )
        )

        val day = model.getNext5DForecast(Locale.UK).single()

        assertEquals("5", day.minTempStr)
        assertEquals("16", day.maxTempStr)
    }

    @Test
    fun `5-day forecast uses the slot nearest midday for the icon and temperature`() {
        // Forecast slots are fixed at 00, 03 ... 21 UTC. For example, in Taipei (UTC+8) they fall on
        // 02, 05 ... 23 local time, so there is no 12 o'clock slot and 11 o'clock is the nearest
        val tomorrow = now.toLocalDate().plusDays(1)
        val model = weatherUiModel(
            offset = taipei,
            forecastItems = listOf(2, 5, 8, 11, 14, 17, 20, 23).map { hour ->
                item(
                    tomorrow.atTime(hour, 0),
                    offset = taipei,
                    temp = hour.toDouble(),
                    icon = if (hour == 11) "10d" else "01n"
                )
            }
        )

        val day = model.getNext5DForecast(Locale.TRADITIONAL_CHINESE).single()

        assertEquals("11", day.tempStr)
        assertTrue(day.icon.endsWith("10d@2x.png"))
    }

    // --- Helpers ---

    private fun weatherUiModel(
        now: LocalDateTime = this.now,
        offset: ZoneOffset = utc,
        name: String? = "London",
        country: String? = "GB",
        forecastItems: List<ForecastItem> = emptyList(),
        selectedLocationName: String? = null,
    ) = WeatherUiModel(
        currentWeather = CurrentWeather(
            main = main(10.0),
            dt = now.toEpochSecond(offset),
            timezone = offset.totalSeconds,
            name = name,
            sys = country?.let { Sys(country = it) }
        ),
        forecast = Forecast(list = forecastItems),
        selectedLocationName = selectedLocationName
    )

    private fun slots(date: LocalDate, vararg hours: Int) =
        hours.map { item(date.atTime(it, 0), temp = 10.0) }

    private fun item(
        dateTime: LocalDateTime,
        offset: ZoneOffset = utc,
        temp: Double,
        min: Double = temp,
        max: Double = temp,
        icon: String = "01d"
    ) = ForecastItem(
        dt = dateTime.toEpochSecond(offset),
        main = main(temp, min, max),
        weather = listOf(Weather(icon = icon))
    )

    // The API returns Kelvin
    private fun main(celsius: Double, min: Double = celsius, max: Double = celsius) = Main(
        temp = celsius + TEMP_K_OFFSET,
        feelsLike = celsius + TEMP_K_OFFSET,
        tempMin = min + TEMP_K_OFFSET,
        tempMax = max + TEMP_K_OFFSET
    )
}
