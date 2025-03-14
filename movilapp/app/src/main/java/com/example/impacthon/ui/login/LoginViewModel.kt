package com.example.impacthon.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.impacthon.backend.models.Usuario
import com.google.gson.Gson

class LoginViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    // Método para establecer el estado de inicio de sesión
    fun setUserLoggedIn(user: Usuario) {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true) // Guardar el estado de inicio de sesión
            putString("usuario", Gson().toJson(user)) // Guardar el objeto Usuario como JSON
            apply() // Aplicar los cambios
        }
    }
}
