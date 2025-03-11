package com.example.impacthon.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.ui.settings.SettingsViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificar si el ViewModel es de tipo SettingsViewModel
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            // Crear y devolver una nueva instancia de SettingsViewModel
            return SettingsViewModel(context) as T
        }
        // Lanzar una excepción si el ViewModel no es reconocido
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
