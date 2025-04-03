package com.example.impacthon.backend.models

import com.google.gson.annotations.SerializedName

data class MapboxResponse(
    val type: String,
    val features: List<MapboxFeature>
)

data class MapboxFeature(
    val type: String,
    val id: String,
    val geometry: Geometry,
    val properties: MapboxProperties
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

data class MapboxProperties(
    @SerializedName("full_address")
    val fullAddress: String
    // Puedes incluir otros campos si lo requieres
)
