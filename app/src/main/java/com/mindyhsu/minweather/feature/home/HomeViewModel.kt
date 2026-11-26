package com.mindyhsu.minweather.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindyhsu.minweather.R
import com.mindyhsu.minweather.data.repository.LocationRepository
import com.mindyhsu.minweather.data.repository.WeatherRepository
import com.mindyhsu.minweather.model.Result
import com.mindyhsu.minweather.model.geo.LocationInfo
import com.mindyhsu.minweather.model.weather.WeatherUiModel
import com.mindyhsu.minweather.feature.search.SEARCH_DEBOUNCE_MS
import com.mindyhsu.minweather.feature.search.SearchUiState
import com.mindyhsu.minweather.feature.search.isLongEnoughToSearch
import com.mindyhsu.minweather.utils.emptyString
import com.mindyhsu.minweather.feature.weather.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode = _isSearchMode.asStateFlow()

    // --- Weather ---
    private val _weatherUiModel = MutableStateFlow<WeatherUiModel?>(null)
    private val _locationDesRes = MutableStateFlow<Int?>(null)
    private val _isLoading = MutableStateFlow(false)

    val weatherUiState: StateFlow<WeatherUiState> =
        combine(
            _weatherUiModel,
            _locationDesRes,
            _isLoading
        ) { data, desRes, isLoading ->
            WeatherUiState(
                data = data,
                locationDesRes = desRes,
                isLoading = isLoading
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep active for 5s after last subscriber
            initialValue = WeatherUiState()
        )

    // --- Search ---
    private val _searchQuery = MutableStateFlow(emptyString())
    private val _isSearching = MutableStateFlow(false)
    private val _searchResults = MutableStateFlow<List<LocationInfo>>(emptyList())
    private val _recentLocations = MutableStateFlow<List<LocationInfo>>(emptyList())

    val searchUiState: StateFlow<SearchUiState> =
        combine(
            _searchQuery,
            _isSearching,
            _searchResults,
            _recentLocations
        ) { query, searching, results, recent ->
            SearchUiState(
                query = query,
                isSearching = searching,
                results = results,
                recent = recent
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep active for 5s after last subscriber
            initialValue = SearchUiState()
        )

    init {
        observeSearchQuery()
    }

    // Search after the user stops typing, and only for text long enough to be a place name.
    // Leaving search mode cancels a pending search; new text cancels a search still in flight.
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeSearchQuery() {
        _isSearchMode
            .flatMapLatest { inSearch ->
                if (!inSearch) emptyFlow()
                else _searchQuery
                    .debounce(SEARCH_DEBOUNCE_MS.milliseconds)
                    .distinctUntilChanged()
                    .mapLatest { text -> search(text) }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun search(text: String) {
        if (!isLongEnoughToSearch(text)) {
            _searchResults.value = emptyList()
            return
        }

        _isSearching.value = true
        try {
            when (val result = locationRepository.searchPlace(locationName = text, limit = 5)) {
                is Result.Success -> _searchResults.value = result.data
                is Result.Error -> Timber.d("[HomeViewModel] search result error")
                is Result.Fail -> Timber.d("[HomeViewModel] search result fail")
                is Result.Loading -> Timber.d("[HomeViewModel] search loading")
            }
        } finally {
            _isSearching.value = false
        }
    }

    fun enterSearchMode() {
        // Clear the text first, so the search flow never starts with old text
        _searchQuery.value = emptyString()
        _searchResults.value = emptyList()
        _isSearchMode.value = true
    }

    fun exitSearchMode() {
        _isSearchMode.value = false
        _searchQuery.value = emptyString()
        _searchResults.value = emptyList()
    }

    fun handlePermissionResult(isGranted: Boolean) {
        Timber.d("[handlePermissionResult] isGranted=$isGranted, data=${_weatherUiModel.value != null}, loading=${_isLoading.value}")
        if (isGranted) {
            // Called on every ON_START, so skip if weather is already shown (e.g. a searched city) or loading
            if (_weatherUiModel.value != null || _isLoading.value) return
            _locationDesRes.value = null
            getCurrentLocation()
        } else {
            _locationDesRes.value = R.string.location_status_unable
        }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = locationRepository.getCurrentLocation()) {
                is Result.Success -> {
                    loadWeatherData(
                        result.data.latitude.toString(),
                        result.data.longitude.toString()
                    )
                }

                is Result.Error -> {
                    Timber.d("[WeatherViewModel] getCurrentLocation result error")
                    _locationDesRes.value = R.string.location_status_unable
                }

                is Result.Fail -> {
                    Timber.d("[WeatherViewModel] getCurrentLocation result fail")
                    _locationDesRes.value = R.string.location_status_unable
                }

                is Result.Loading -> {
                    Timber.d("[WeatherViewModel] getCurrentLocation loading")
                }
            }

            _isLoading.value = false
        }
    }

    private fun loadWeatherData(
        latitude: String,
        longitude: String,
        selectedLocationName: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val currentWeather = weatherRepository.getCurrentWeather(latitude, longitude)
            val forecast = weatherRepository.get5DaysForecast(latitude, longitude)

            if (currentWeather is Result.Success && forecast is Result.Success) {
                _weatherUiModel.update {
                    WeatherUiModel(currentWeather.data, forecast.data, selectedLocationName)
                }
            } else {
                Timber.d("[WeatherViewModel] loadWeatherData result error")
            }

            _isLoading.value = false
        }
    }

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
        if (!isLongEnoughToSearch(text)) {
            _searchResults.value = emptyList()
        }
    }

    fun onResultClick(item: LocationInfo) {
        addRecent(item)
        loadWeatherData(
            latitude = item.lat.toString(),
            longitude = item.lon.toString(),
            selectedLocationName = listOfNotNull(item.name, item.country).joinToString(", ")
        )
        exitSearchMode()
    }

    // TODO: Implement room database to store recent searches
    private fun addRecent(location: LocationInfo) {
        _recentLocations.update {
            val list = it.toMutableList()
            list.remove(location)
            list.add(0, location)
            list.take(5)
        }
    }
}