package com.example.impacthon.ui.profile

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.R
import com.example.impacthon.backend.api.RetrofitClient
import com.example.impacthon.backend.models.Local
import com.example.impacthon.backend.models.Opinion
import com.example.impacthon.backend.models.Usuario
import com.example.impacthon.databinding.FragmentProfileBinding
import com.example.impacthon.ui.ViewModelFactory
import com.example.impacthon.ui.login.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            usuario = it.getParcelable("usuario")!!
        }

        // Verifica si el usuario no es nulo antes de mostrar los detalles
        usuario?.let {
            binding.textFullName.text = it.nombre
            binding.textNickname.text = it.nickname
            binding.profileContainer.visibility = View.VISIBLE

            fetchOpiniones(it.nickname)
        } ?: run {
            // Manejo si el usuario es nulo
            usuario = profileViewModel.usuarioLogueado()

            binding.textFullName.text = usuario!!.nombre
            binding.textNickname.text = usuario!!.nickname
            binding.profileContainer.visibility = View.VISIBLE

            fetchOpiniones(usuario!!.nickname)
        }
    }

    private fun fetchOpiniones(nickname: String) {
        RetrofitClient.instance.getOpinionesPorUsuario(nickname).enqueue(object : Callback<List<Opinion>> {
            override fun onResponse(
                call: Call<List<Opinion>>,
                response: Response<List<Opinion>>
            ) {
                if (response.isSuccessful) {
                    val opinionesList = response.body() ?: emptyList()
                    updateOpinionesUI(opinionesList)
                } else {
                    Toast.makeText(
                        context,
                        "Error al obtener opiniones",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<List<Opinion>>,
                t: Throwable
            ) {
                Toast.makeText(
                    context,
                    "Fallo en la petición: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateOpinionesUI(opinionesList: List<Opinion>) {
        val reviewsContainer = binding.reviewsContainer
        reviewsContainer.removeAllViews() // Limpiar las opiniones anteriores

        if (opinionesList.isEmpty()) {
            val noOpinionesText = TextView(context).apply {
                text = "No hay opiniones disponibles."
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            reviewsContainer.addView(noOpinionesText)
        } else {
            for (opinion in opinionesList) {
                val opinionLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 20, 0, 20)
                }

                // Obtener el nombre del local usando el ID
                obtainLocal(opinion.local.id) { nombreLocal ->
                    val usuarioText = TextView(context).apply {
                        text = nombreLocal // Aquí se imprime el nombre del local
                        setTypeface(null, Typeface.BOLD)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    }

                    val reseñaText = TextView(context).apply {
                        text = "${opinion.resenaTexto}"
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    }

                    val divider = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
                        val typedValue = TypedValue()
                        context.theme.resolveAttribute(com.mapbox.maps.R.attr.colorPrimary, typedValue, true)
                        setBackgroundColor(typedValue.data)
                    }

                    opinionLayout.addView(usuarioText)
                    opinionLayout.addView(reseñaText)
                    opinionLayout.addView(divider)

                    reviewsContainer.addView(opinionLayout)
                }
            }
        }
    }

    private fun obtainLocal(id: Int, callback: (String) -> Unit) {
        RetrofitClient.instance.getLocal(id).enqueue(object : Callback<Local> {
            override fun onResponse(call: Call<Local>, response: Response<Local>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!.nombre)
                } else {
                    Toast.makeText(context, "Error al obtener el local", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Local>, t: Throwable) {
                Toast.makeText(context, "Fallo en la petición: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
