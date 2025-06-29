package com.jd.raincheckapp.presentation.viewmodel

import java.time.LocalDate

sealed class WeatherUiState {
    data object Initial : WeatherUiState()
    data object LoadingLocation : WeatherUiState()
    data object LoadingWeather : WeatherUiState()
    data class Success(
        val cityName: String,
        val forecastDate: LocalDate,
        val willRain: Boolean,
        val rainProbability: Int,
        val temperature: Double,
        val humidity: Int,
        val weatherDescription: String,
        val lastUpdated: String
    ) : WeatherUiState()

    data class Error(val message: String) : WeatherUiState()
    data class NoForecast(val message: String) : WeatherUiState()
}
