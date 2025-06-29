package com.jd.raincheckapp.data.api

import com.jd.raincheckapp.data.model.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApiService {

    @GET("forecast")
    suspend fun getFiveDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherForecastResponse
}
