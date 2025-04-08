package com.example.impacthon.backend.models

import com.google.gson.annotations.SerializedName

data class Opinion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("usuario")
    val usuario: UsuarioForOpinion,
    @SerializedName("local")
    val local: LocalForOpinion,
    @SerializedName("fechaPublicacion")
    val fechaPublicacion: String,
    @SerializedName("resenaTexto")
    val resenaTexto: String,
    @SerializedName("ecosostenible")
    val ecosostenible: Int,
    @SerializedName("inclusionSocial")
    val inclusionSocial: Int,
    @SerializedName("accesibilidad")
    val accesibilidad: Int,
    @SerializedName("foto")
    val foto: String?
    //val fotos: List<ByteArray>
)

data class UsuarioForOpinion(
    @SerializedName("nickname")
    val nickname: String
)

data class LocalForOpinion(
    @SerializedName("id")
    val id: Int
)
