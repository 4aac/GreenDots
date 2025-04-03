package com.example.impacthon.backend.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapboxRetrofitClient {
    private const val BASE_URL = "https://api.mapbox.com"

    val instance: MapboxApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapboxApiService::class.java)
    }
}
