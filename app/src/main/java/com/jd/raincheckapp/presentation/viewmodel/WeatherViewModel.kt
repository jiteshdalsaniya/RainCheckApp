package com.jd.raincheckapp.presentation.viewmodel

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.jd.raincheckapp.data.model.ForecastItem
import com.jd.raincheckapp.data.repository.WeatherRepository
import com.jd.raincheckapp.utils.Constants
import com.jd.raincheckapp.utils.WeatherEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val fusedLocationClient: FusedLocationProviderClient // Injected for location
) : ViewModel() {

    private val _forecastList = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecastList: StateFlow<List<ForecastItem>> = _forecastList

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val uiState: StateFlow<WeatherUiState> = _uiState

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now().plusDays(1))

    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val _locationName = MutableStateFlow("Detecting location...")
    val locationName: StateFlow<String> = _locationName

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        // Re-fetch weather for the new date
        _currentLocation.value?.let { fetchWeatherForecast(it.latitude, it.longitude, date) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCurrentLocation() {
        _uiState.value = WeatherUiState.LoadingLocation
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    _currentLocation.value = location
                    fetchLocationName(location.latitude, location.longitude)
                    fetchWeatherForecast(location.latitude, location.longitude, _selectedDate.value)
                } else {
                    _uiState.value =
                        WeatherUiState.Error("Location not found. Please enable GPS or try again.")
                }
            }.addOnFailureListener { e ->
                _uiState.value =
                    WeatherUiState.Error("Failed to get location: ${e.localizedMessage}")
            }
        } catch (e: SecurityException) {
            _uiState.value =
                WeatherUiState.Error("Location permissions denied. Please grant permissions.")
        } catch (e: Exception) {
            _uiState.value =
                WeatherUiState.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }

    private fun fetchLocationName(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _locationName.value = "Detecting location..."
            val result = repository.getCityName(latitude, longitude)
            result.onSuccess { locationResponse ->
                _locationName.value = locationResponse.address.neighbourhood
                    ?: locationResponse.address.county
                            ?: "Unknown Location"
            }.onFailure { e ->
                _locationName.value = "Failed to get location name"
                _uiState.value =
                    WeatherUiState.Error("Failed to get location name: ${e.localizedMessage}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeatherForecast(latitude: Double, longitude: Double, date: LocalDate) {
        _uiState.value = WeatherUiState.LoadingWeather
        viewModelScope.launch {
            val result = repository.getWeatherForecast(
                latitude,
                longitude,
                Constants.OPEN_WEATHER_API_KEY
            )
            result.onSuccess { response ->
                val targetEpochSecond = date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
                val forecastForSelectedDate = response.list.firstOrNull { forecastItem ->
                    val forecastDate = Instant.ofEpochSecond(forecastItem.dt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    forecastDate == date
                }

                if (forecastForSelectedDate != null) {
                    _uiState.value = WeatherUiState.Success(
                        cityName = _locationName.value,
                        forecastDate = date,
                        willRain = repository.willItRain(forecastForSelectedDate),
                        rainProbability = (forecastForSelectedDate.pop * 100).toInt(),
                        temperature = forecastForSelectedDate.main.temp,
                        humidity = forecastForSelectedDate.main.humidity,
                        weatherDescription = forecastForSelectedDate.weather.firstOrNull()?.description
                            ?: "N/A",
                        lastUpdated = getTimeAgo(System.currentTimeMillis()) // This would ideally come from API response or actual fetch time
                    )
                } else {
                    _uiState.value =
                        WeatherUiState.NoForecast("No forecast available for selected date.")
                }

            }.onFailure { e ->
                _uiState.value =
                    WeatherUiState.Error("Failed to fetch weather: ${e.localizedMessage}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateDisplay(date: LocalDate): String {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        return when (date) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> date.format(DateTimeFormatter.ofPattern("EEEE")) // Full weekday name
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchSevenDayForecast() {
        val location = _currentLocation.value
        if (location == null) {
            _uiState.value = WeatherUiState.Error("Location not available for forecast.")
            return
        }

        viewModelScope.launch {
            val result = repository.getWeatherForecast(
                location.latitude,
                location.longitude,
                Constants.OPEN_WEATHER_API_KEY
            )
            result.onSuccess { response ->
                val grouped = response.list.groupBy {
                    Instant.ofEpochSecond(it.dt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }

                val sevenDayList = grouped.entries
                    .sortedBy { it.key }
                    .take(7)
                    .mapNotNull { it.value.firstOrNull() }

                _forecastList.value = sevenDayList
            }.onFailure { e ->
                _uiState.value =
                    WeatherUiState.Error("Failed to fetch 7-day forecast: ${e.localizedMessage}")
            }
        }
    }

    fun showPermissionError() {
        _uiState.value = WeatherUiState.Error(WeatherEvent.PERMISSION_DENIED)
    }

    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
            hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
            days == 1L -> "Yesterday"
            days < 7 -> "$days day${if (days == 1L) "" else "s"} ago"
            else -> {
                // fallback to full date format
                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                "Last updated on ${sdf.format(Date(timestamp))}"
            }
        }
    }
}