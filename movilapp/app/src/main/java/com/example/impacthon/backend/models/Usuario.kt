package com.example.impacthon.backend.models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Usuario(
    val nickname: String,
    @SerializedName("nombreCompleto") val nombre: String,
    val email: String,
    val password: String,
    @SerializedName("fechaCreacion") val fechaCreacion: Timestamp,
    val admin: Boolean,
    val fotoPerfil: ByteArray?, // El JSON lo envía como null, por lo que se usa ByteArray?
    val favoritos: List<Local>
)
