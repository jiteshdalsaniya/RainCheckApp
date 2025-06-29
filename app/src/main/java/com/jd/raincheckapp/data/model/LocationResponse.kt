package com.jd.raincheckapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationResponse(
    @Json(name = "address")
    val address: Address,
    @Json(name = "addresstype")
    val addresstype: String,
    @Json(name = "boundingbox")
    val boundingbox: List<String>,
    @Json(name = "class")
    val classX: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "importance")
    val importance: Double,
    @Json(name = "lat")
    val lat: String,
    @Json(name = "licence")
    val licence: String,
    @Json(name = "lon")
    val lon: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "osm_id")
    val osmId: Int,
    @Json(name = "osm_type")
    val osmType: String,
    @Json(name = "place_id")
    val placeId: Int,
    @Json(name = "place_rank")
    val placeRank: Int,
    @Json(name = "type")
    val type: String
)

@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "country")
    val country: String,
    @Json(name = "country_code")
    val countryCode: String,
    @Json(name = "county")
    val county: String,
    @Json(name = "ISO3166-2-lvl4")
    val iSO31662Lvl4: String,
    @Json(name = "neighbourhood")
    val neighbourhood: String,
    @Json(name = "postcode")
    val postcode: String,
    @Json(name = "state")
    val state: String,
    @Json(name = "state_district")
    val stateDistrict: String
)