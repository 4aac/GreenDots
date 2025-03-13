package com.example.impacthon.backend.models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Usuario(
    val nickname: String,
    @SerializedName("nombreCompleto") val nombre: String,
    val email: String,
    val password: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    val admin: Boolean,
    val fotoPerfil: ByteArray?, // El JSON lo envía como null, por lo que se usa ByteArray?
) {
    // Función para verificar si el usuario es administrador
    fun esAdmin(): Boolean {
        return admin
    }

    // Función para obtener una representación en cadena del usuario sin la contraseña
    override fun toString(): String {
        return "Usuario(nickname='$nickname', nombre='$nombre', email='$email', fechaCreacion='$fechaCreacion', admin=$admin)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Usuario

        if (admin != other.admin) return false
        if (nickname != other.nickname) return false
        if (nombre != other.nombre) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (fechaCreacion != other.fechaCreacion) return false
        if (fotoPerfil != null) {
            if (other.fotoPerfil == null) return false
            if (!fotoPerfil.contentEquals(other.fotoPerfil)) return false
        } else if (other.fotoPerfil != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = admin.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + nombre.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + fechaCreacion.hashCode()
        result = 31 * result + (fotoPerfil?.contentHashCode() ?: 0)
        return result
    }
}