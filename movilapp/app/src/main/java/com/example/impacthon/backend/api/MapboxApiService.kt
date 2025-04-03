package com.example.impacthon.backend.api

import com.example.impacthon.backend.models.MapboxResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapboxApiService {
    @GET("/search/geocode/v6/reverse")
    fun reverseGeocode(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("access_token") accessToken: String
    ): Call<MapboxResponse>
}
