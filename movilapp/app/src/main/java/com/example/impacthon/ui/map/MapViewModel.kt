package com.example.impacthon.ui.map

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.impacthon.backend.models.Usuario
import com.google.gson.Gson

class MapViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    fun nicknameUsuario(): String {
        val usuario = Gson().fromJson(sharedPreferences.getString("usuario", "")!!, Usuario::class.java)
        return usuario.nickname
    }


}
