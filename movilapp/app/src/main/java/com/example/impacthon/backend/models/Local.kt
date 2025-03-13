package com.example.impacthon.backend.models

import com.google.gson.annotations.SerializedName

data class Local(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("categoria")
    val categoria: String,

    // Puedes optar por usar String o Timestamp según convenga en tu proyecto.
    @SerializedName("fechaAdmision")
    val fechaAdmision: String,

    @SerializedName("ubicacion")
    val ubicacion: String,

    @SerializedName("descripcionTextual")
    val descripcionTextual: String?,

    @SerializedName("ecosostenible")
    val ecosostenible: Int,

    @SerializedName("inclusionSocial")
    val inclusionSocial: Int,

    @SerializedName("accesibilidad")
    val accesibilidad: Int
)
