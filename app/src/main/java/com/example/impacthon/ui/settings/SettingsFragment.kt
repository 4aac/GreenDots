package com.example.impacthon.ui.settings

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.MainActivity
import com.example.impacthon.databinding.FragmentSettingsBinding
import com.example.impacthon.ui.ViewModelFactory
import java.util.Locale


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this, ViewModelFactory(requireContext())).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configurar los botones de idioma
        val buttonEspañol: Button = binding.buttonEspanhol
        val buttonGalego: Button = binding.buttonGalego
        val buttonEnglish: Button = binding.buttonEnglish

        buttonEspañol.setOnClickListener {
            settingsViewModel.changeLanguage("es") // Cambia el idioma a español
            setLocale("es") // Cambia el idioma de la aplicación
        }

        buttonGalego.setOnClickListener {
            settingsViewModel.changeLanguage("gl") // Cambia el idioma a gallego
            setLocale("gl") // Cambia el idioma de la aplicación
        }

        buttonEnglish.setOnClickListener {
            settingsViewModel.changeLanguage("en") // Cambia el idioma a inglés
            setLocale("en") // Cambia el idioma de la aplicación
        }


        // Acceder al repositorio
        val externalLinkButton: Button = binding.buttonGithub
        externalLinkButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/martindios/impacthon"))
            startActivity(intent)
        }

        // Cambiar modo oscuro
        val darkModeSwitch: Switch = binding.darkModeSwitch
        val modeIcon: ImageView = binding.modeIcon

        settingsViewModel.isDarkMode.observe(viewLifecycleOwner) { isDarkMode ->
            darkModeSwitch.isChecked = isDarkMode
            // Establecer el modo de tema inicial
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            // Actualizar el icono
            updateModeIcon(isDarkMode, modeIcon)
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            // Actualizar el ícono según el estado del modo oscuro
            updateModeIcon(isChecked, modeIcon)
        }

        return root
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        // Crea un nuevo contexto con el idioma deseado
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        // Crea un nuevo contexto con la configuración actualizada
        val context = requireActivity().createConfigurationContext(config)
        // Actualiza los recursos de la actividad
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)
        // Llama a un método para actualizar la interfaz de usuario
        updateUI()
        // Actualizar el BottomNavigationView
        (requireActivity() as MainActivity).updateBottomNavigationView()
    }

    private fun updateUI() {
        // Obtiene las referencias a los elementos de la interfaz de usuario
        val textSettings = requireView().findViewById<TextView>(com.example.impacthon.R.id.text_settings)
        val textLanguages = requireView().findViewById<TextView>(com.example.impacthon.R.id.text_languages)
        val buttonEspañol = requireView().findViewById<Button>(com.example.impacthon.R.id.button_espanhol)
        val buttonGalego = requireView().findViewById<Button>(com.example.impacthon.R.id.button_galego)
        val buttonEnglish = requireView().findViewById<Button>(com.example.impacthon.R.id.button_english)
        val buttonGithub = requireView().findViewById<Button>(com.example.impacthon.R.id.button_github)


        // Actualiza el texto de cada elemento con las cadenas correspondientes
        textSettings.setText(com.example.impacthon.R.string.title_settings)
        textLanguages.setText(com.example.impacthon.R.string.title_language)
        buttonEspañol.setText(com.example.impacthon.R.string.title_español)
        buttonGalego.setText(com.example.impacthon.R.string.title_galego)
        buttonEnglish.setText(com.example.impacthon.R.string.title_english)
        buttonGithub.setText(com.example.impacthon.R.string.title_github)
    }

    private fun updateModeIcon(isDarkMode: Boolean, modeIcon: ImageView) {
        modeIcon.setImageResource(
            if (isDarkMode) com.example.impacthon.R.drawable.ic_moon_white_24dp else com.example.impacthon.R.drawable.ic_sun_black_24dp // Reemplaza con tus íconos de luna y sol
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}