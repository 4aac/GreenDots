package com.example.impacthon.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.ui.login.LoginViewModel
import com.example.impacthon.ui.map.MapViewModel
import com.example.impacthon.ui.profile.ProfileViewModel
import com.example.impacthon.ui.settings.SettingsViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificar si el ViewModel es de tipo ProfileViewModel
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)) as T
        }
        // Verificar si el ViewModel es de tipo LoginViewModel
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)) as T
        }
        // Verificar si el ViewModel es de tipo SettingsViewModel
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(context) as T
        }
        // Verificar si el ViewModel es de tipo MapViewModel
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)) as T
        }
        // Lanzar una excepción si el ViewModel no es reconocido
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
