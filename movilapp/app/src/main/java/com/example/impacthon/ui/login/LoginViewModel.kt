package com.example.impacthon.ui.login

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.impacthon.backend.models.Usuario
import com.google.gson.Gson

class LoginViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    // Método para verificar las credenciales de inicio de sesión
    fun login(username: String, password: String): Boolean {
        // Verificar las credenciales (en este caso, usuario y contraseña son "sabela")
        return username == "sabela" && password == "sabela"
    }

    // Método para establecer el estado de inicio de sesión
    fun setUserLoggedIn(user: Usuario) {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true) // Guardar el estado de inicio de sesión
            putString("usuario", Gson().toJson(user)) // Guardar el objeto Usuario como JSON
            apply() // Aplicar los cambios
        }
    }

    // Método para verificar si el usuario ha iniciado sesión
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false) // Devuelve false si no se ha encontrado el valor
    }
}
