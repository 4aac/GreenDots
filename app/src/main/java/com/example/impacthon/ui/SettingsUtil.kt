package com.example.impacthon.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object SettingsUtil {

    fun applySettings(context: Context) {
        val prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        // Aplicar idioma
        val language = prefs.getString("selected_language", "es") ?: "es"
        setLocale(context, language)
        // Aplicar modo oscuro
        val darkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setLocale(context: Context, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
