package com.mindyhsu.minweather.data.remote

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mindyhsu.minweather.model.Result
import com.mindyhsu.minweather.model.geo.LocationInfo
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

class LocationDataSourceImpl @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val apiService: WeatherApiService
) : LocationDataSource {

    override suspend fun getCurrentLocation(): Result<Location> =
        // The location task may never complete (e.g. permission granted while the app is running),
        // so time out instead of waiting forever
        withTimeoutOrNull(LOCATION_TIMEOUT_MILLIS.milliseconds) { requestCurrentLocation() }
            ?: Result.Fail("Location request timed out")

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocation(): Result<Location> =
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            // Stop the location request if the calling coroutine is cancelled
            continuation.invokeOnCancellation { cancellationTokenSource.cancel() }

            // Unlike lastLocation, getCurrentLocation still returns a location when there is no cached one
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(Result.Success(location))
                    } else {
                        continuation.resume(Result.Fail("Location is null"))
                    }
                }
                .addOnFailureListener {
                    Timber.d("[LocationDataSourceImpl] getCurrentLocation error: $it")
                    continuation.resume(Result.Error(it))
                }
                .addOnCanceledListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.Fail("Location request cancelled"))
                    }
                }
        }

    override suspend fun fetchLocationInfo(
        locationName: String,
        limit: Int?
    ): List<LocationInfo> {
        return apiService.getGeocoding(locationName, limit)
    }

    companion object {
        private const val LOCATION_TIMEOUT_MILLIS = 10_000L
    }
}