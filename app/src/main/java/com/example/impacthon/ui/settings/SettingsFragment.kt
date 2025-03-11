package com.example.impacthon.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.databinding.FragmentSettingsBinding
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

        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Cambiar idioma
        val languageButton: Button = binding.languageOption
        languageButton.setOnClickListener {
            // Lógica para cambiar el idioma
            // Por ejemplo, abrir un diálogo para seleccionar el idioma
        }

        // Acceder al repositorio
        val externalLinkButton: Button = binding.externalLinkButton
        externalLinkButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/martindios/impacthon"))
            startActivity(intent)
        }

        // Cambiar modo oscuro
        val darkModeSwitch: Switch = binding.darkModeSwitch
        settingsViewModel.isDarkMode.observe(viewLifecycleOwner) { isDarkMode ->
            darkModeSwitch.isChecked = isDarkMode
            // Establecer el modo de tema inicial
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.toggleDarkMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}