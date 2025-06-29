package com.jd.raincheckapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

@JsonClass(generateAdapter = true)
data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val pop: Double, // Probability of Precipitation
    val rain: Rain?,
    @Json(name = "dt_txt") val dtTxt: String
)

@JsonClass(generateAdapter = true)
data class Main(
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    val humidity: Int
)

@JsonClass(generateAdapter = true)
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class Rain(
    @Json(name = "3h") val threeHour: Double?
)

@JsonClass(generateAdapter = true)
data class City(
    val id: Long,
    val name: String,
    val country: String
)