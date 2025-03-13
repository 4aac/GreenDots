package com.example.impacthon.ui.map

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.impacthon.R
import com.example.impacthon.backend.api.RetrofitClient
import com.example.impacthon.backend.models.Local
import com.example.impacthon.backend.models.LocalForOpinion
import com.example.impacthon.backend.models.Opinion
import com.example.impacthon.backend.models.Usuario
import com.example.impacthon.backend.models.UsuarioForOpinion
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.colorResource
import androidx.activity.compose.rememberLauncherForActivityResult
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import java.io.ByteArrayOutputStream
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView

class MapFragment : Fragment() {
    private var permissionsGranted by mutableStateOf(false)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    /**
     * Función para obtener un Local por su ID usando Retrofit.
     * Recibe el id, el contexto y un callback para devolver el resultado.
     */
    private fun fetchLocalById(id: Int, context: Context, onResult: (Local?) -> Unit) {
        RetrofitClient.instance.getLocal(id).enqueue(object : Callback<Local> {
            override fun onResponse(call: Call<Local>, response: Response<Local>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body())
                } else {
                    Toast.makeText(context, "Error al obtener el local", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
            override fun onFailure(call: Call<Local>, t: Throwable) {
                Log.e("RetrofitError", "Fallo en la petición", t)
                Toast.makeText(context, "Fallo en la petición: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
        })
    }

    @Composable
    fun SearchBarDropdown(
        modifier: Modifier = Modifier,
        onCitySelected: (String) -> Unit
    ) {
        AndroidView(
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.search_bar, null, false).apply {
                    val spinner = findViewById<Spinner>(R.id.city_spinner)
                    val cities = context.resources.getStringArray(R.array.cities_array).toMutableList()

                    cities.add(0, "Seleccionar una ciudad")
                    // Crea un ArrayAdapter con un layout personalizado para el ítem seleccionado
                    val adapter = ArrayAdapter(context, R.layout.spinner_items, cities).apply {
                        // Establece el layout para los ítems desplegados
                        setDropDownViewResource(R.layout.spinner_items)
                    }
                    spinner.adapter = adapter

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position > 0) {
                                val city = parent?.getItemAtPosition(position) as String
                                onCitySelected(city)
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) { }
                    }
                }
            },
            modifier = modifier
        )
    }

    /**
     * Función para obtener todos los locales usando Retrofit.
     */
    private fun fetchAllLocales(context: Context, onResult: (List<Local>?) -> Unit) {
        RetrofitClient.instance.getAllLocales().enqueue(object : Callback<List<Local>> {
            override fun onResponse(call: Call<List<Local>>, response: Response<List<Local>>) {
                if (response.isSuccessful && response.body() != null) {
                    onResult(response.body())
                } else {
                    Toast.makeText(context, "Error al obtener los locales", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
            override fun onFailure(call: Call<List<Local>>, t: Throwable) {
                Log.e("RetrofitError", "Fallo en la petición", t)
                Toast.makeText(context, "Fallo en la petición: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
        })
    }

    // Función de prueba (puedes eliminarla si ya no la usas)
    private fun createTestUser(context: Context) {
        val newUser = Usuario(
            nickname = "testuser",
            nombre = "Test User",
            email = "test@example.com",
            password = "123456",
            fechaCreacion = "2025-03-12T12:00:00.000+0000",
            admin = false,
            fotoPerfil = null
        )
        RetrofitClient.instance.createUser(newUser).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al crear usuario: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("RetrofitError", "Fallo petición", t)
                Toast.makeText(context, "Fallo en la petición: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función auxiliar para parsear la cadena de ubicación al par (latitud, longitud)
    private fun parseLocation(location: String): Pair<Double, Double>? {
        val parts = location.split(",")
        return if (parts.size == 2) {
            val lat = parts[0].trim().toDoubleOrNull()
            val lng = parts[1].trim().toDoubleOrNull()
            if (lat != null && lng != null) Pair(lat, lng) else null
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                onPermissionResult(granted)
            }
        permissionsGranted = PermissionsManager.areLocationPermissionsGranted(requireContext())
        if (!permissionsGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent { MapScreenWithPermissions(permissionsGranted) }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        permissionsGranted = granted
        if (granted) {
            view?.findViewById<ComposeView>(android.R.id.content)?.setContent { MapScreenWithPermissions(true) }
        } else {
            Toast.makeText(
                requireContext(),
                "Los permisos de ubicación han sido denegados. Algunas funciones estarán deshabilitadas.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Composable
    fun MapScreenWithPermissions(permissionsGranted: Boolean) {
        if (permissionsGranted) {
            MapScreen()
        } else {
            PermissionRequestScreen()
        }
    }

    @Composable
    fun PermissionRequestScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Se requieren permisos de ubicación para mostrar tu posición en el mapa")
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                ) { Text("Solicitar Permisos") }
            }
        }
    }

    @Composable
    fun MapScreen() {
        val context = LocalContext.current

        // Estados y lógica existente
        var showMarkerInfo by remember { mutableStateOf(false) }
        val markerLocal = remember { mutableStateOf<Local?>(null) }
        var allLocales by remember { mutableStateOf<List<Local>>(emptyList()) }
        var showNewOpinionForm by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            fetchAllLocales(context) { locales ->
                locales?.let { allLocales = it }
            }
        }

        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.5)
                center(Point.fromLngLat(-3.74922, 40.463667))
                pitch(0.0)
                bearing(-10.0)
                padding(EdgeInsets(1200.0, 0.0, 0.0, 0.0))
            }
        }
        val mapboxMapRef = remember { mutableStateOf<MapboxMap?>(null) }
        var animationStarted by remember { mutableStateOf(false) }

        LaunchedEffect(mapboxMapRef.value) {
            if (mapboxMapRef.value != null && !animationStarted) {
                animationStarted = true
                delay(1500L)
                mapboxMapRef.value?.flyTo(
                    cameraOptions {
                        center(Point.fromLngLat(-7.868491393372855, 41.88448973513718))
                        zoom(6.0)
                        pitch(15.0)
                        bearing(0.0)
                    },
                    mapAnimationOptions { duration(6000) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MapboxMap(

                    scaleBar = { },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    mapState = rememberMapState {
                        gesturesSettings = GesturesSettings {
                            pinchToZoomEnabled = true
                            doubleTapToZoomInEnabled = true
                            quickZoomEnabled = true
                            doubleTouchToZoomOutEnabled = false
                        }
                    },
                    mapViewportState = mapViewportState,
                    style = { MapStyle(style = "mapbox://styles/martindios/cm851rhgo004q01qzbcrg0fb9") }
                ) {
                    val markerResourceId = R.drawable.red_marker
                    val markerIcon = rememberIconImage(key = markerResourceId, painter = painterResource(markerResourceId))
                    allLocales.forEach { local ->
                        parseLocation(local.ubicacion)?.let { (lat, lng) ->
                            PointAnnotation(
                                point = Point.fromLngLat(lng, lat)
                            ) {
                                iconImage = markerIcon
                                interactionsState.onClicked {
                                    markerLocal.value = local
                                    showMarkerInfo = true
                                    true
                                }
                            }
                        }
                    }
                    MapEffect(Unit) { mapView ->
                        mapboxMapRef.value = mapView.mapboxMap
                        mapView.location.updateSettings {
                            locationPuck = createDefault2DPuck(withBearing = true)
                            puckBearing = PuckBearing.HEADING
                            puckBearingEnabled = true
                            enabled = true
                        }

                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    // Llama a tu método para centrar el mapa en la ubicación actual
                    mapViewportState.transitionToFollowPuckState()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 86.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Ir a mi ubicación",
                    modifier = Modifier.size(24.dp)
                )
            }
            SearchBarDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = 50.dp),  // Ajusta según lo necesites
                onCitySelected = { city ->
                    // Define las coordenadas para cada ciudad
                    val (lng, lat) = when (city) {
                        "Vigo" -> Pair(-8.720, 42.240)
                        "Pontevedra" -> Pair(-8.640, 42.431)
                        "A Coruña" -> Pair(-8.406, 43.362)
                        "Santiago" -> Pair(-8.546, 42.878)
                        else -> Pair(-3.74922, 40.463667)
                    }
                    animateFlyTo(mapboxMapRef, lng, lat)
                }
            )

            if (showMarkerInfo && markerLocal.value != null) {
                MarkerInfoSheet(
                    local = markerLocal.value!!,
                    onClose = { showMarkerInfo = false },
                    onAddOpinion = { showNewOpinionForm = true }
                )
            }
        }

        if (showNewOpinionForm && markerLocal.value != null) {
            NewOpinionFormDialog(local = markerLocal.value!!, onDismiss = { showNewOpinionForm = false })
        }
    }

    fun animateFlyTo(mapboxMapRef: MutableState<MapboxMap?>, lng: Double, lat: Double) {
        mapboxMapRef.value?.flyTo(
            cameraOptions {
                center(Point.fromLngLat(lng, lat))
                zoom(13.5)
                pitch(75.0)
                bearing(130.0)
            },
            mapAnimationOptions { duration(4500) },
            animatorListener = object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    mapboxMapRef.value?.flyTo(
                        cameraOptions {
                            center(Point.fromLngLat(lng, lat))
                            zoom(13.5)
                            pitch(0.0)
                            bearing(0.0)
                        },
                        mapAnimationOptions { duration(1750) }
                    )
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }
        )
    }




    @Composable
    fun MarkerInfoSheet(local: Local, onClose: () -> Unit, onAddOpinion: () -> Unit) {
        var showOpinionsDialog by remember { mutableStateOf(false) }
        var opinionesList by remember { mutableStateOf<List<Opinion>>(emptyList()) }
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // Ocupa el 50% de la pantalla, puedes ajustar según el diseño
                .background(MaterialTheme.colors.surface)
                .padding(top = 64.dp) // Para no estar encima de la barra de estado
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Título y botón de cierre
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Información del Lugar", style = MaterialTheme.typography.h6)
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }
                Divider()

                // Aquí los botones de las opiniones, ahora arriba
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onAddOpinion) {
                        Text("Añadir Opinión")
                    }
                    Button(onClick = {
                        opinionesList = emptyList()
                        RetrofitClient.instance.getOpinionesPorLocal(local.id)
                            .enqueue(object : Callback<List<Opinion>> {
                                override fun onResponse(
                                    call: Call<List<Opinion>>,
                                    response: Response<List<Opinion>>
                                ) {
                                    if (response.isSuccessful) {
                                        opinionesList = response.body() ?: emptyList()
                                        showOpinionsDialog = true
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
                    }) {
                        Text("Ver Opiniones")
                    }
                }

                // Aquí es donde insertamos las InfoCards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()) // Permite desplazarse si hay mucho contenido
                        .padding(8.dp)
                ) {
                    InfoCard(
                        nombre = local.nombre,
                        categoria = local.categoria,
                        ubicacion = local.ubicacion,
                        ecosostenible = local.ecosostenible,
                        accesibilidad = local.accesibilidad,
                        inclusion_social = local.inclusionSocial
                    )
                }
            }

            // Mostrar el diálogo de opiniones si se activa
            if (showOpinionsDialog) {
                AlertDialog(
                    onDismissRequest = { showOpinionsDialog = false },
                    title = { Text("Opiniones del Local") },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            if (opinionesList.isEmpty()) {
                                Text("No hay opiniones disponibles.")
                            } else {
                                opinionesList.forEach { opinion ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text(
                                            text = "Usuario: ${opinion.usuario.nickname}",
                                            style = MaterialTheme.typography.subtitle2
                                        )
                                        Text(
                                            text = "Reseña: ${opinion.resenaTexto}",
                                            style = MaterialTheme.typography.body2
                                        )
                                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showOpinionsDialog = false }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    }



    @Composable
    fun InfoCard(
        nombre: String,
        categoria: String,
        ubicacion: String,
        ecosostenible: Int,
        accesibilidad: Int,
        inclusion_social: Int
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre tarjetas
        ) {
            // Tarjeta de Nombre
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Tarjeta más fina
                elevation = 6.dp,
                backgroundColor = colorResource(id = R.color.white)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Nombre:",
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.3f)
                    )
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Tarjeta de Categoría
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Tarjeta más fina
                elevation = 6.dp,
                backgroundColor = colorResource(id = R.color.white)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Categoría:",
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.3f)
                    )
                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Tarjeta de Ubicación
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Tarjeta más fina
                elevation = 6.dp,
                backgroundColor = colorResource(id = R.color.white)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ubicación:",
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.3f)
                    )
                    Text(
                        text = ubicacion,
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.black),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Tarjeta de Ecosostenible
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Tarjeta más fina
                elevation = 6.dp,
                backgroundColor = colorResource(id = R.color.white)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Ecosostenible:",
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.3f)
                    )
                    // Mostrar el puntaje del ecosostenible
                    Text(
                        text = "$ecosostenible/5",
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Tarjeta de Accesibilidad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Tarjeta más fina
                elevation = 6.dp,
                backgroundColor = colorResource(id = R.color.white)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Accesibilidad:",
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.3f)
                    )
                    // Mostrar el puntaje de accesibilidad
                    Text(
                        text = "$accesibilidad/5",
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }

            // Tarjeta de Inclusión Social
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Tarjeta más fina
                elevation = 6.dp,
                backgroundColor = colorResource(id = R.color.white)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Inclusión Social:",
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.3f)
                    )
                    // Mostrar el puntaje de inclusión social
                    Text(
                        text = "$inclusion_social/5",
                        style = MaterialTheme.typography.body2,
                        color = colorResource(id = R.color.black),
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }
        }
    }

    @Composable
    fun NewOpinionFormDialog(
        local: com.example.impacthon.backend.models.Local,
        onDismiss: () -> Unit
    ) {
        var reviewText by remember { mutableStateOf("") }
        var ecosostenible by remember { mutableStateOf(0f) }
        var inclusionSocial by remember { mutableStateOf(0f) }
        var accesibilidad by remember { mutableStateOf(0f) }
        val context = LocalContext.current

        // Estado para mostrar el diálogo de selección de fuente de imagen
        var showImageSourceDialog by remember { mutableStateOf(false) }
        // Estado para almacenar la imagen seleccionada como ByteArray
        var selectedImageByteArray by remember { mutableStateOf<ByteArray?>(null) }

        // Launcher para capturar imagen desde la cámara (preview)
        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            bitmap?.let {
                val outputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                selectedImageByteArray = outputStream.toByteArray()
            }
        }

        // Launcher para seleccionar imagen desde la galería
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                // Lee el contenido del Uri y lo guarda en un ByteArray
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    selectedImageByteArray = inputStream.readBytes()
                }
            }
        }

        // Función para convertir el ByteArray a Base64 (si es necesario enviarlo como string)
        fun byteArrayToBase64(bytes: ByteArray): String {
            return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
        }

        // Genera la fecha actual en formato ISO, por ejemplo "2025-03-12T12:30:00.000+0000"
        val formattedDate = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            Locale.getDefault()
        ).format(Date())

        // Diálogo para elegir la fuente de la imagen
        if (showImageSourceDialog) {
            AlertDialog(
                onDismissRequest = { showImageSourceDialog = false },
                title = { Text("Selecciona Fuente de Imagen") },
                text = { Text("Elige entre tomar una foto o seleccionar desde la galería") },
                confirmButton = {
                    Button(onClick = {
                        showImageSourceDialog = false
                        cameraLauncher.launch(null) // Abre la cámara
                    }) {
                        Text("Cámara")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch("image/*") // Abre la galería
                    }) {
                        Text("Galería")
                    }
                }
            )
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Añadir Opinión") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Reseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Ecosostenible: ${ecosostenible.toInt()}")
                    Slider(
                        value = ecosostenible,
                        onValueChange = { ecosostenible = it },
                        valueRange = 0f..5f,
                        steps = 4
                    )
                    Text("Inclusión Social: ${inclusionSocial.toInt()}")
                    Slider(
                        value = inclusionSocial,
                        onValueChange = { inclusionSocial = it },
                        valueRange = 0f..5f,
                        steps = 4
                    )
                    Text("Accesibilidad: ${accesibilidad.toInt()}")
                    Slider(
                        value = accesibilidad,
                        onValueChange = { accesibilidad = it },
                        valueRange = 0f..5f,
                        steps = 4
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Botón único para añadir foto
                    Button(onClick = { showImageSourceDialog = true }) {
                        Text("Añadir Foto")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Muestra un mensaje simple según si se ha seleccionado una imagen
                    if (selectedImageByteArray != null) {
                        Text("Imagen seleccionada (${selectedImageByteArray!!.size} bytes)")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Se convierte el ByteArray a Base64 y se guarda en la lista de fotos (si se seleccionó imagen)
                    val fotosList = if (selectedImageByteArray != null)
                        listOf(byteArrayToBase64(selectedImageByteArray!!))
                    else emptyList()

                    val newOpinion = Opinion(
                        id = 0, // El backend generará el id
                        usuario = UsuarioForOpinion(nickname = "juan23"), // Valor de prueba
                        local = LocalForOpinion(id = local.id),
                        fechaPublicacion = formattedDate,
                        resenaTexto = reviewText,
                        ecosostenible = ecosostenible.toInt(),
                        inclusionSocial = inclusionSocial.toInt(),
                        accesibilidad = accesibilidad.toInt(),
                        fotos = fotosList
                    )
                    Log.e(
                        "NewOpinion",
                        "id: ${newOpinion.id}, usuario: ${newOpinion.usuario.nickname}, local: ${newOpinion.local.id}, " +
                                "fechaPublicacion: ${newOpinion.fechaPublicacion}, resenaTexto: ${newOpinion.resenaTexto}, " +
                                "ecosostenible: ${newOpinion.ecosostenible}, inclusionSocial: ${newOpinion.inclusionSocial}, " +
                                "accesibilidad: ${newOpinion.accesibilidad}, fotos: ${newOpinion.fotos}"
                    )

                    RetrofitClient.instance.createOpinion(newOpinion)
                        .enqueue(object : Callback<String> {
                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Opinión enviada exitosamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error al enviar opinión",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(
                                    context,
                                    "Fallo en la petición: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    onDismiss()
                }) {
                    Text("Enviar Opinión")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}