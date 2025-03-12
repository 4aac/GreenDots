package com.example.impacthon.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class ProfileViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    // Método para cerrar sesión
    fun logout() {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", false) // Cambiar el estado de inicio de sesión a false
            apply() // Aplicar los cambios
        }
    }
}

