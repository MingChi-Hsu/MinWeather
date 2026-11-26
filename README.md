# MinWeather
[![CI](https://github.com/MingChi-Hsu/MinWeather/actions/workflows/ci.yml/badge.svg)](https://github.com/MingChi-Hsu/MinWeather/actions/workflows/ci.yml) ![](https://img.shields.io/badge/Platform-Android-green) ![](https://img.shields.io/badge/Language-Kotlin-purple)

MinWeather is a side project built with Jetpack Compose to demonstrate modern Android development practices. It follows the MVVM + Repository Pattern, ensuring a clean architecture with clear separation and maintainability. The app also integrates essential Android tools, including Hilt for dependency injection, Retrofit and Kotlinx Serialization for API and data handling. 

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
- Type-ahead suggestions (autocomplete) for faster, more accurate results

<img src ="doc/search.gif" alt="isolated" width="200"/>

### Light & Dark Mode
- Fully supports system light and dark themes

<img src ="doc/light_dark_mode.gif" alt="isolated" width="200"/>

### Localisation
- App text and weather descriptions in English and Traditional Chinese; other languages fall back to English
- Dates and times follow the device's language and region (e.g. 13/11 in the UK, 11/13 in the US)

## Technical Highlights
### Architecture
- Used **MVVM** and the **Repository Pattern** for clear separation of concerns
- Managed UI state with **ViewModel** and **StateFlow**
- Implemented dependency injection with **Hilt**

### UI
- Built the UI with **Jetpack Compose** and **Material3** for a modern, responsive experience
- Supported **Adaptive App Icons** and **Themed App Icons**

### Data
- Integrated **REST APIs** with **Retrofit** and parsed JSON with **Kotlinx Serialization**
- Retrieved the user's location with **FusedLocationProviderClient**
- Loaded images with **Coil**

### Quality & Delivery
- Wrote **unit tests** with **JUnit** and **kotlinx-coroutines-test**, using fake repositories
- Shrank and obfuscated release builds with **R8**
- Set up **CI/CD** with **GitHub Actions**: lint, unit tests and build on every push, and signed release bundles from version tags, uploaded to Google Play closed testing automatically

## Release Note
| Version | Date       | Description |
| ------- |------------| ----------- |
| 1.0.3 | 2026-11-19 | - Smaller app size with R8 code and resource shrinking |
| 1.0.2 | 2026-11-18 | - Weather descriptions follow the app language (Traditional Chinese or English)<br>- Dates and times follow the device's language and region |
| 1.0.1 | 2026-11-11 | - Fixed the 5-day forecast showing the same high and low temperature<br>- Fixed the 5-day forecast showing night-time icons<br>- Fixed the location name shown after picking a searched place |
| 1.0.0 | 2026-11-08 | - First release on Google Play (closed testing)<br>- Added Traditional Chinese localisation<br>- Added OpenWeather attribution with logo and link<br>- Updated app icons and added a monochrome icon for themed icons<br>- Upgraded target SDK to 36 (Android 16) |
| | 2026-09-26 | - Fixed location not being retrieved right after installation (no cached last location)<br>- Fixed the "Unable to access location" message flashing before the permission dialog appears<br>- Fixed weather failing to load when the API omits optional fields (missing values now show the placeholder). |
|  | 2025-08-25 | MVP development |

## APIs
- OpenWeather
  - [Current Weather](https://openweathermap.org/current)
  - [5 Day / 3 Hour Forecast](https://openweathermap.org/forecast5)
  - [Geocoding](https://openweathermap.org/api/geocoding-api)
  - Weather data provided by [OpenWeather](https://openweathermap.org/)

## Libraries
- [Jetpack Compose](https://developer.android.com/jetpack/compose) – Modern UI toolkit
- [Hilt](https://dagger.dev/hilt/) – Dependency Injection
- [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) + [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) – Networking & JSON
- [Coil](https://github.com/coil-kt/coil) – Image loading
- [Google Play Services Location](https://developers.google.com/android/guides/overview) – Device location
- [Accompanist Permissions](https://google.github.io/accompanist/permissions/) – Runtime permissions in Compose
- [Timber](https://github.com/JakeWharton/timber) – Logging

## Fonts
- [jf open 粉圓](https://github.com/justfont/open-huninn-font) – SIL Open Font License 1.1
- [Iansui 芫荽](https://github.com/ButTaiwan/iansui) – SIL Open Font License 1.1

## Requirements
- Android Studio Narwhal Feature Drop | 2025.1.2 or newer
- Android Gradle Plugin 8.12.2
- Gradle 8.13 (via gradle-wrapper)
- Kotlin 1.9.0
- Min SDK 28, Target SDK 36 (Android 16)

## Privacy Policy
- [English](https://mingchi-hsu.github.io/app-doc/minweather/privacy-policy)
- [繁體中文](https://mingchi-hsu.github.io/app-doc/minweather/privacy-policy-zh-tw)

## Wish List
- Star / favorite multiple cities using Room Database
- Support Jetpack Navigation for multi-screen navigation
- Add automated tests (Unit / UI / screenshot test)
- Display weather forecasts with charts
- Widget
- Talkback
- Add an About page with open source licences
- Reload weather when the system language changes, so weather descriptions switch language straight away

## Contact
[Mindy Hsu](https://www.linkedin.com/in/ming-chi-hsu/)
