package com.jd.raincheckapp.data.repository

import com.jd.raincheckapp.data.api.NominatimApiService
import com.jd.raincheckapp.data.api.OpenWeatherMapApiService
import com.jd.raincheckapp.data.model.ForecastItem
import com.jd.raincheckapp.data.model.LocationResponse
import com.jd.raincheckapp.data.model.WeatherForecastResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val openWeatherMapApiService: OpenWeatherMapApiService,
    private val nominatimApiService: NominatimApiService
) {
    suspend fun getCityName(latitude: Double, longitude: Double): Result<LocationResponse> {
        return try {
            val response = nominatimApiService.reverseGeocode(latitude, longitude)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeatherForecast(latitude: Double, longitude: Double, apiKey: String): Result<WeatherForecastResponse> {
        return try {
            val response = openWeatherMapApiService.getFiveDayForecast(latitude, longitude, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun willItRain(forecastItem: ForecastItem): Boolean {
        return forecastItem.pop > 0.3 ||
                forecastItem.weather.any { it.main in listOf("Rain", "Drizzle", "Thunderstorm") }
    }
}