package com.example.impacthon.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.impacthon.backend.models.Usuario
import com.google.gson.Gson

class ProfileViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun usuarioLogueado(): Usuario {
        val usuarioGuardado = sharedPreferences.getString("usuario", "")!!
        return Gson().fromJson(usuarioGuardado, Usuario::class.java)
    }

    // Método para cerrar sesión
    fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", false) // Cambiar el estado de inicio de sesión a false
            apply() // Aplicar los cambios
        }
    }
}

