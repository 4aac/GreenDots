package com.example.impacthon.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    private val _text = MutableLiveData<String>().apply {
        value = "Configuraciones"
    }
    val text: LiveData<String> = _text

    // LiveData para el idioma
    private val _selectedLanguage = MutableLiveData<String>().apply {
        value = "es" // Valor predeterminado (español)
    }
    val selectedLanguage: LiveData<String> = _selectedLanguage

    // Método para cambiar el idioma
    fun changeLanguage(language: String) {
        _selectedLanguage.value = language
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