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
import com.example.impacthon.databinding.FragmentProfileBinding
import com.example.impacthon.ui.ViewModelFactory
import com.example.impacthon.ui.login.LoginFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

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
        binding.profileContainer.visibility = View.VISIBLE
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
