package com.jd.raincheckapp.data.api

import com.jd.raincheckapp.data.model.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): LocationResponse
}