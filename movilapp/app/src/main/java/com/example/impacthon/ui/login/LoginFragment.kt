package com.example.impacthon.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.impacthon.R
import com.example.impacthon.ui.ViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("my_prefs", AppCompatActivity.MODE_PRIVATE)

        // Crear el ViewModelFactory y el ViewModel
        val factory = ViewModelFactory(requireContext())
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        // Configurar el botón de inicio de sesión
        val loginButton: Button = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val username: EditText = view.findViewById(R.id.textfield_username)
            val password: EditText = view.findViewById(R.id.textfield_passwd)

            val user = username.text.toString()
            val pass = password.text.toString()

            // Intentar iniciar sesión
            if (loginViewModel.login(user, pass)) {
                // Si el inicio de sesión es exitoso, actualizar el estado de inicio de sesión
                loginViewModel.setUserLoggedIn()

                // Navegar al ProfileFragment
                findNavController().navigate(R.id.navigation_profile) // Regresar al ProfileFragment
            } else {
                // Mostrar un mensaje de error
                Toast.makeText(requireContext(), R.string.error_login, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
