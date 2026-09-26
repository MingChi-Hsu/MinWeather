# Min Weather
![](https://img.shields.io/badge/Platform-Android-green) ![](https://img.shields.io/badge/Language-Kotlin-purple) ![](https://img.shields.io/badge/License-MIT-white)

Min Weather is a side project built with Jetpack Compose to demonstrate modern Android development practices. It follows the MVVM + Repository Pattern, ensuring a clean architecture with clear separation and maintainability. The app also integrates essential Android tools, including Hilt for dependency injection, Retrofit and Kotlinx Serialization for API and data handling. 

## Features
### Current Location Weather
- Automatically fetches the current weather for your location

### Rich Weather Details
- Displays a wide variety of weather conditions including temperature, feels-like temperature, humidity, probability of precipitation (PoP), wind speed, visibility, sunrise and sunset

### Forecasts
- 24-hour forecast in 3-hour intervals
- 5-day forecast at a glance

<img src ="doc/location_weather_details.gif" alt="isolated" width="200"/>

### City Search
- Enables city-name search to view weather details
- Typed-ahead suggestions (autocomplete) for faster, more accurate results

<img src ="doc/search.gif" alt="isolated" width="200"/>

### Light & Dark Mode
- Fully supports system light and dark themes

<img src ="doc/light_dark_mode.gif" alt="isolated" width="200"/>

## Technical Highlights
- Used **MVVM**, **Repository Pattern** and **Clean Architecture** for scalability and maintainability
- Built the UI with **Jetpack Compose** and **Material3** for a modern, responsive experience
- Managed UI state with **ViewModel** and **StateFlow**
- Integrated **REST APIs** with **Retrofit** and parsed JSON with **Kotlinx Serialization** to streamline data flow
- Implemented dependency injection with **Hilt**
- Loaded images with **Coil**
- Retrieved the user's location with **FusedLocationProviderClient**
- Supported **Adaptive App Icons** and **Themed App Icons**

## Release Note
| Version | Date | Description |
| ------- | ---- | ----------- |
| | 2026-09-26 | - Fixed location not being retrieved right after installation (no cached last location)<br>- Fixed the "Unable to access location" message flashing before the permission dialog appears<br>- Fixed weather failing to load when the API omits optional fields (missing values now show the placeholder). |
|  | 2025-08-25 | MVP development |

## APIs
- OpenWeather
  - [Current Weather](https://openweathermap.org/current)
  - [5 Day / 3 Hour Forecast](https://openweathermap.org/forecast5)
  - [Geocoding](https://openweathermap.org/api/geocoding-api)

## Libraries
- [Jetpack Compose](https://developer.android.com/jetpack/compose) – Modern UI toolkit
- [Hilt](https://dagger.dev/hilt/) – Dependency Injection
- [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) + [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) – Networking & JSON
- [Coil](https://github.com/coil-kt/coil) – Image loading
- [Google Play Services Location](https://developers.google.com/android/guides/overview) – Device location
- [Timber](https://github.com/JakeWharton/timber) – Logging


## Requirements
- Android Studio Narwhal | 2025.1.2
- Android Gradle Plugin 8.4.0
- Gradle 8.6 (via gradle-wrapper)
- Kotlin 1.9.0
- SDK version 28+

## Wish List
- Star / favorite multiple cities using Room Database
- Support Jetpack Navigation for multi-screen navigation
- Integrate CI/CD into the development workflow to improve efficiency
- Add automated tests (Unit / UI / screenshot test)
- Display weather forecasts with charts
- Widget

## Contact
[Mindy Hsu](https://www.linkedin.com/in/ming-chi-hsu/)
