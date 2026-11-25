package com.mindyhsu.minweather.data.repository

import android.location.Location
import com.mindyhsu.minweather.model.Result
import com.mindyhsu.minweather.model.geo.LocationInfo
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class FakeLocationRepository : LocationRepository {

    var currentLocationResult: Result<Location> = Result.Success(Location("test"))
    var searchResult: Result<List<LocationInfo>> = Result.Success(emptyList())

    override suspend fun getCurrentLocation(): Result<Location> = currentLocationResult

    // Every query sent to searchPlace, in order, so tests can check when and what was searched
    val searchedQueries = mutableListOf<String>()

    // Like a real network call, the result can arrive after the caller has moved on
    var searchDelayMs = 0L

    override suspend fun searchPlace(locationName: String, limit: Int?): Result<List<LocationInfo>> {
        searchedQueries += locationName
        delay(searchDelayMs.milliseconds)
        return searchResult
    }
}
