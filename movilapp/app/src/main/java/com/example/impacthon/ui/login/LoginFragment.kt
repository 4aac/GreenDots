package com.example.impacthon.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.example.impacthon.backend.api.RetrofitClient
import com.example.impacthon.backend.models.Local
import com.example.impacthon.backend.models.Usuario
import com.example.impacthon.ui.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences
    var usuario: Usuario? = null

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

            val credentials: Map<String, String> = mapOf("nickname" to user, "password" to pass)

            // Intentar iniciar sesión
            RetrofitClient.instance.login(credentials).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful && response.body() != null) {
                        // Si el inicio de sesión es exitoso, actualizar el estado de inicio de sesión
                        usuario = response.body()
                        usuario?.let { it1 -> loginViewModel.setUserLoggedIn(it1) }

                        val bundle = Bundle()
                        bundle.putParcelable("usuario", usuario)

                        // Navegar al ProfileFragment
                        findNavController().navigate(R.id.navigation_profile, bundle) // Regresar al ProfileFragment
                    } else {
                        Toast.makeText(context, "Error al obtener el Usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(requireContext(), R.string.error_login, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
