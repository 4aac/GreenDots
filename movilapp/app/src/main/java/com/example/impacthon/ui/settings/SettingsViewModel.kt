package com.example.impacthon.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(context: Context) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // LiveData para el idioma
    private val _selectedLanguage = MutableLiveData<String>().apply {
        value = sharedPreferences.getString("selected_language", "es") // Valor predeterminado (español)
    }

    // Método para cambiar el idioma
    fun changeLanguage(language: String) {
        _selectedLanguage.value = language
        // Guardar el idioma en SharedPreferences
        with(sharedPreferences.edit()) {
            putString("selected_language", language)
            apply()
        }
    }

    // LiveData para el estado del modo claro/oscuro
    private val _isDarkMode = MutableLiveData<Boolean>().apply {
        value = sharedPreferences.getBoolean("dark_mode", false) // Cargar el valor guardado
    }
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    // Método para cambiar el estado del modo oscuro
    fun toggleDarkMode(isEnabled: Boolean) {
        _isDarkMode.value = isEnabled
        // Guardar el estado en SharedPreferences
        with(sharedPreferences.edit()) {
            putBoolean("dark_mode", isEnabled)
            apply()
        }
    }
}