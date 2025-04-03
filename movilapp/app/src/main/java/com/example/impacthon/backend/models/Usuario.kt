package com.example.impacthon.backend.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Usuario(
    val nickname: String,
    @SerializedName("nombreCompleto") val nombre: String,
    val email: String,
    val password: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    val admin: Boolean,
    val fotoPerfil: String? // El JSON lo envía como null, por lo que se usa ByteArray?
) : Parcelable {

    // Función para verificar si el usuario es administrador
    fun esAdmin(): Boolean {
        return admin
    }

    // Función para obtener una representación en cadena del usuario sin la contraseña
    override fun toString(): String {
        return "Usuario(nickname='$nickname', nombre='$nombre', email='$email', fechaCreacion='$fechaCreacion', admin=$admin)"
    }

    // Implementación de Parcelable
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nickname)
        parcel.writeString(nombre)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(fechaCreacion)
        parcel.writeByte(if (admin) 1 else 0)
        parcel.writeString(fotoPerfil)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Usuario> {
        override fun createFromParcel(parcel: Parcel): Usuario {
            return Usuario(
                nickname = parcel.readString() ?: "",
                nombre = parcel.readString() ?: "",
                email = parcel.readString() ?: "",
                password = parcel.readString() ?: "",
                fechaCreacion = parcel.readString() ?: "",
                admin = parcel.readByte() != 0.toByte(),
                fotoPerfil = parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<Usuario?> {
            return arrayOfNulls(size)
        }
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
        result = 31 * result + fotoPerfil.hashCode()
        return result
    }
}
