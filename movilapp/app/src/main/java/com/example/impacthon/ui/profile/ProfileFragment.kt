package com.example.impacthon.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.R
import com.example.impacthon.backend.models.Usuario
import com.example.impacthon.databinding.FragmentProfileBinding
import com.example.impacthon.ui.ViewModelFactory
import com.example.impacthon.ui.login.LoginFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel
    private var usuario: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Crear el ViewModelFactory
        val factory = ViewModelFactory(requireContext())
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        // Verificar si el usuario ha iniciado sesión
        if (profileViewModel.isUserLoggedIn()) {
            showProfileDetails()
        } else {
            showLoginFragment()
        }

        // Configurar el botón de cierre de sesión
        binding.buttonLogout.setOnClickListener {
            profileViewModel.logout() // Llamar al método de logout
            showLoginFragment() // Navegar de vuelta al LoginFragment
        }

        return binding.root
    }

    private fun showProfileDetails() {
        arguments?.let {
            usuario = it.getParcelable("usuario")!! // Asegúrate de que Usuario es Parcelable
        }

        // Verifica si el usuario no es nulo antes de mostrar los detalles
        usuario?.let {
            binding.textFullName.text = it.nombre // Rellenar el TextView con el nombre
            binding.textNickname.text = it.nickname // Rellenar el TextView con el nickname
            binding.profileContainer.visibility = View.VISIBLE
        } ?: run {
            // Si el usuario es nulo, puedes manejarlo aquí
            // Por ejemplo, ocultar el contenedor del perfil
            usuario = profileViewModel.usuarioLogueado()

            binding.textFullName.text = usuario!!.nombre // Rellenar el TextView con el nombre
            binding.textNickname.text = usuario!!.nickname // Rellenar el TextView con el nickname
            binding.profileContainer.visibility = View.VISIBLE
            //binding.profileContainer.visibility = View.GONE
        }

    }

    private fun showLoginFragment() {
        binding.profileContainer.visibility = View.GONE

        val loginFragment = LoginFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.login_container, loginFragment)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
