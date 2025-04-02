package com.example.impacthon.ui.profile

import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
                        R.string.text_no_opinions,
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

    private fun formatDate(dateString: String): String {
        // Definir el formato de entrada
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        // Definir el formato de salida
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault() // Ajustar a la zona horaria local

        return try {
            val date = inputFormat.parse(dateString) // Parsear la fecha
            outputFormat.format(date) // Formatear la fecha en el nuevo formato
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // Retornar el string original en caso de error
        }
    }

    private fun updateOpinionesUI(opinionesList: List<Opinion>) {
        val reviewsContainer = binding.reviewsContainer
        reviewsContainer.removeAllViews() // Limpiar las opiniones anteriores

        if (opinionesList.isEmpty()) {
            val noOpinionesText = TextView(context).apply {
                text = getString(R.string.text_no_opinions)
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
                    setPadding(0, 30, 0, 10)
                }

                // Obtener el nombre del local usando el ID
                obtainLocal(opinion.local.id) { nombreLocal ->
                    val usuarioText = TextView(context).apply {
                        text = nombreLocal
                        setTypeface(null, Typeface.BOLD)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    }

                    val reseñaText = TextView(context).apply {
                        text = opinion.resenaTexto
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    }

                    // Crear un contenedor para las valoraciones
                    val valoracionesLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL // Mantener la orientación vertical para las valoraciones
                    }

                    // Función para agregar estrellas con su respectivo nombre
                    fun addRatingWithStars(label: String, rating: Int) {
                        // Crear un contenedor horizontal para el nombre y las estrellas
                        val ratingLayout = LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            setPadding(0, 10, 0, 10) // Espaciado opcional
                        }

                        // Crear un TextView para el nombre de la valoración
                        val labelText = TextView(context).apply {
                            text = label
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                            setTypeface(null, Typeface.ITALIC)
                            layoutParams = LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1f
                            )
                        }
                        ratingLayout.addView(labelText)

                        // Crear un contenedor para las estrellas
                        val starsLayout = LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                        }

                        // Agregar estrellas
                        for (i in 1..5) {
                            val star = ImageView(context).apply {
                                setImageResource(if (i <= rating) R.drawable.star_filled_24dp else R.drawable.star_empty_24dp)
                                val typedValue = TypedValue()
                                context.theme.resolveAttribute(com.mapbox.maps.R.attr.colorPrimary, typedValue, true)
                                setColorFilter(typedValue.data)
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            }
                            starsLayout.addView(star)
                        }
                        ratingLayout.addView(starsLayout)
                        valoracionesLayout.addView(ratingLayout)
                    }

                    addRatingWithStars(getString(R.string.title_ecosustainable), opinion.ecosostenible)
                    addRatingWithStars(getString(R.string.title_socialinclusion), opinion.inclusionSocial)
                    addRatingWithStars(getString(R.string.title_accessibility), opinion.accesibilidad)

                    val dateText = TextView(context).apply {
                        text = formatDate(opinion.fechaPublicacion)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                    }

                    val divider = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5)
                        val typedValue = TypedValue()
                        context.theme.resolveAttribute(com.mapbox.maps.R.attr.colorPrimary, typedValue, true)
                        setBackgroundColor(typedValue.data)
                    }

                    val space = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10)
                    }

                    opinionLayout.addView(usuarioText)
                    opinionLayout.addView(reseñaText)
                    opinionLayout.addView(space)
                    opinionLayout.addView(valoracionesLayout)
                    opinionLayout.addView(dateText)
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
