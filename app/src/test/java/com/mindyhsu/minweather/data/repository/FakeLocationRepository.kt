package com.mindyhsu.minweather.data.repository

import android.location.Location
import com.mindyhsu.minweather.model.Result
import com.mindyhsu.minweather.model.geo.LocationInfo

class FakeLocationRepository : LocationRepository {

    var currentLocationResult: Result<Location> = Result.Success(Location("test"))
    var searchResult: Result<List<LocationInfo>> = Result.Success(emptyList())

    override suspend fun getCurrentLocation(): Result<Location> = currentLocationResult

    override suspend fun searchPlace(locationName: String, limit: Int?): Result<List<LocationInfo>> =
        searchResult
}
