package com.mindyhsu.minweather.feature.home

import com.mindyhsu.minweather.R
import com.mindyhsu.minweather.data.repository.FakeLocationRepository
import com.mindyhsu.minweather.data.repository.FakeWeatherRepository
import com.mindyhsu.minweather.model.Result
import com.mindyhsu.minweather.model.geo.LocationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

/**
 * Characterisation tests: they record how HomeViewModel behaves today,
 * so the Clean Architecture refactor can be checked against them.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var locationRepository: FakeLocationRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        // viewModelScope runs on Dispatchers.Main, which does not exist in JVM tests
        Dispatchers.setMain(testDispatcher)
        weatherRepository = FakeWeatherRepository()
        locationRepository = FakeLocationRepository()
        viewModel = HomeViewModel(weatherRepository, locationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Current location ---

    @Test
    fun `granting location permission loads weather for the current location`() = runTest(testDispatcher) {
        collectUiStates()

        viewModel.handlePermissionResult(isGranted = true)
        advanceUntilIdle()

        val state = viewModel.weatherUiState.value
        assertNotNull(state.data)
        assertFalse(state.isLoading)
        assertNull(state.locationDesRes)
    }

    @Test
    fun `granting permission again does not reload weather that is already shown`() = runTest(testDispatcher) {
        collectUiStates()

        // handlePermissionResult runs on every ON_START, e.g. when the user comes back to the app
        viewModel.handlePermissionResult(isGranted = true)
        advanceUntilIdle()
        viewModel.handlePermissionResult(isGranted = true)
        advanceUntilIdle()

        assertEquals(1, weatherRepository.currentWeatherCalls)
    }

    @Test
    fun `denying location permission shows the unable to access location message`() = runTest(testDispatcher) {
        collectUiStates()

        viewModel.handlePermissionResult(isGranted = false)

        assertEquals(R.string.location_status_unable, viewModel.weatherUiState.value.locationDesRes)
    }

    @Test
    fun `failing to get the location shows the unable to access location message`() = runTest(testDispatcher) {
        locationRepository.currentLocationResult = Result.Fail("Location is null")
        collectUiStates()

        viewModel.handlePermissionResult(isGranted = true)
        advanceUntilIdle()

        val state = viewModel.weatherUiState.value
        assertEquals(R.string.location_status_unable, state.locationDesRes)
        assertFalse(state.isLoading)
    }

    @Test
    fun `failing to load weather leaves no weather data`() = runTest(testDispatcher) {
        weatherRepository.currentWeatherResult = Result.Error(Exception("Network error"))
        collectUiStates()

        viewModel.handlePermissionResult(isGranted = true)
        advanceUntilIdle()

        val state = viewModel.weatherUiState.value
        assertNull(state.data)
        assertFalse(state.isLoading)
    }

    // --- Search results ---

    @Test
    fun `picking a search result shows its name instead of the API city name`() = runTest(testDispatcher) {
        weatherRepository.currentWeatherResult = Result.Success(FakeWeatherRepository.currentWeather(name = "Song"))
        collectUiStates()
        viewModel.enterSearchMode()

        viewModel.onResultClick(place("Zhongli District", "TW"))
        advanceUntilIdle()

        assertEquals("Zhongli District, TW", viewModel.weatherUiState.value.data?.getLocationName())
        assertFalse(viewModel.isSearchMode.value)
    }

    @Test
    fun `recent places keep the latest pick first, without duplicates, up to 5`() = runTest(testDispatcher) {
        collectUiStates()
        val places = listOf("A", "B", "C", "D", "E", "F").map { place(it, "GB") }

        places.forEach { viewModel.onResultClick(it) }
        viewModel.onResultClick(places[2]) // pick C again

        assertEquals(listOf("C", "F", "E", "D", "B"), viewModel.searchUiState.value.recent.map { it.name })
    }

    // --- Search typing ---
    // These describe the behaviour we want, not the behaviour today (fix/search-debounce)

    @Test
    fun `searches only after the user stops typing for 500 ms`() = runTest(testDispatcher) {
        collectUiStates()
        viewModel.enterSearchMode()

        viewModel.onSearchTextChange("london")
        advanceTimeBy((SEARCH_DEBOUNCE_MS - 1).milliseconds)
        assertEquals(emptyList<String>(), locationRepository.searchedQueries)

        advanceTimeBy(1.milliseconds)
        runCurrent()
        assertEquals(listOf("london"), locationRepository.searchedQueries)
    }

    @Test
    fun `typing quickly searches only the last text`() = runTest(testDispatcher) {
        collectUiStates()
        viewModel.enterSearchMode()

        listOf("lond", "londo", "london").forEach { text ->
            viewModel.onSearchTextChange(text)
            advanceTimeBy(TIME_SHORTER_THAN_SEARCH_DEBOUNCE_MS.milliseconds)
        }
        advanceUntilIdle()

        assertEquals(listOf("london"), locationRepository.searchedQueries)
    }

    @Test
    fun `entering search mode does not search`() = runTest(testDispatcher) {
        collectUiStates()

        viewModel.enterSearchMode()
        advanceUntilIdle()

        assertEquals(emptyList<String>(), locationRepository.searchedQueries)
    }

    @Test
    fun `two Latin letters do not search`() = runTest(testDispatcher) {
        collectUiStates()
        viewModel.enterSearchMode()

        viewModel.onSearchTextChange("lo")
        advanceUntilIdle()

        assertEquals(emptyList<String>(), locationRepository.searchedQueries)
    }

    @Test
    fun `two Chinese characters search, because many place names are only two characters`() = runTest(testDispatcher) {
        collectUiStates()
        viewModel.enterSearchMode()

        viewModel.onSearchTextChange("台北")
        advanceUntilIdle()

        assertEquals(listOf("台北"), locationRepository.searchedQueries)
    }

    @Test
    fun `one Chinese character does not search`() = runTest(testDispatcher) {
        collectUiStates()
        viewModel.enterSearchMode()

        viewModel.onSearchTextChange("台")
        advanceUntilIdle()

        assertEquals(emptyList<String>(), locationRepository.searchedQueries)
    }

    @Test
    fun `deleting text below the minimum length clears the results`() = runTest(testDispatcher) {
        locationRepository.searchResult = Result.Success(listOf(place("London", "GB")))
        locationRepository.searchDelayMs = TIME_SHORTER_THAN_SEARCH_DEBOUNCE_MS // the response comes back after the text has changed
        collectUiStates()
        viewModel.enterSearchMode()
        viewModel.onSearchTextChange("london")
        advanceUntilIdle()

        viewModel.onSearchTextChange("lo")
        advanceUntilIdle()

        assertEquals(emptyList<LocationInfo>(), viewModel.searchUiState.value.results)
    }

    @Test
    fun `leaving search mode before the debounce ends does not search`() = runTest(testDispatcher) {
        collectUiStates()
        viewModel.enterSearchMode()

        viewModel.onSearchTextChange("london")
        advanceTimeBy(TIME_SHORTER_THAN_SEARCH_DEBOUNCE_MS.milliseconds)
        viewModel.exitSearchMode()
        advanceUntilIdle()

        assertEquals(emptyList<String>(), locationRepository.searchedQueries)
    }

    // --- Helpers ---

    // The UI states use WhileSubscribed, so they only update while something collects them
    private fun TestScope.collectUiStates() {
        backgroundScope.launch { viewModel.weatherUiState.collect {} }
        backgroundScope.launch { viewModel.searchUiState.collect {} }
    }

    private fun place(name: String, country: String) =
        LocationInfo(name = name, lat = 0.0, lon = 0.0, country = country)

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 500L
        const val TIME_SHORTER_THAN_SEARCH_DEBOUNCE_MS = 100L
    }
}
