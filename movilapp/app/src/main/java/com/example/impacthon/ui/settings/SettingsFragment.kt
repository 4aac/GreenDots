package com.example.impacthon.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.MainActivity
import com.example.impacthon.R
import com.example.impacthon.databinding.FragmentSettingsBinding
import com.example.impacthon.ui.SettingsUtil
import com.example.impacthon.ui.ViewModelFactory

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

        // Configurar botones de idioma
        binding.buttonEspanhol.setOnClickListener {
            settingsViewModel.changeLanguage("es")
            updateAppConfiguration("es")
        }

        binding.buttonGalego.setOnClickListener {
            settingsViewModel.changeLanguage("gl")
            updateAppConfiguration("gl")
        }

        binding.buttonEnglish.setOnClickListener {
            settingsViewModel.changeLanguage("en")
            updateAppConfiguration("en")
        }

        // Configurar botón de enlace externo
        binding.buttonGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/martindios/GreenDots/"))
            startActivity(intent)
        }

        // Configurar el switch del modo oscuro
        val darkModeSwitch: Switch = binding.darkModeSwitch
        val modeIcon: ImageView = binding.modeIcon

        settingsViewModel.isDarkMode.observe(viewLifecycleOwner) { isDarkMode ->
            darkModeSwitch.isChecked = isDarkMode
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            updateModeIcon(isDarkMode, modeIcon)
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            updateModeIcon(isChecked, modeIcon)
        }

        return root
    }

    private fun updateAppConfiguration(language: String) {
        // Se actualiza la configuración global usando la clase utilitaria
        SettingsUtil.applySettings(requireContext())
        // Actualiza el BottomNavigationView para reflejar el idioma
        (requireActivity() as MainActivity).updateBottomNavigationView()
        // Actualiza la UI del propio fragmento
        updateUI()
    }

    private fun updateUI() {
        binding.textSettings.setText(R.string.title_settings)
        binding.textLanguages.setText(R.string.title_language)
        binding.buttonEspanhol.setText(R.string.title_español)
        binding.buttonGalego.setText(R.string.title_galego)
        binding.buttonEnglish.setText(R.string.title_english)
        binding.buttonGithub.setText(R.string.title_github)
    }

    private fun updateModeIcon(isDarkMode: Boolean, modeIcon: ImageView) {
        modeIcon.setImageResource(
            if (isDarkMode) R.drawable.ic_moon_white_24dp else R.drawable.ic_sun_black_24dp
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
