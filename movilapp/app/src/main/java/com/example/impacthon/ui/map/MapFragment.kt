package com.example.impacthon.ui.map

import android.Manifest
import android.animation.Animator
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.example.impacthon.R
import kotlinx.coroutines.delay
import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.impacthon.backend.models.Usuario
import com.example.impacthon.backend.RetrofitClient
import com.example.impacthon.backend.models.Local

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
            setContent {
                MapScreenWithPermissions(permissionsGranted)
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        permissionsGranted = granted
        if (granted) {
            view?.findViewById<ComposeView>(android.R.id.content)?.setContent {
                MapScreenWithPermissions(true)
            }
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
                ) {
                    Text("Solicitar Permisos")
                }
            }
        }
    }

    @Composable
    fun MapScreen() {
        val context = LocalContext.current

        // Estados para controlar la visibilidad y la información del marker
        var showMarkerInfo by remember { mutableStateOf(false) }
        val markerLocal = remember { mutableStateOf<Local?>(null) }

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
                    mapAnimationOptions {
                        duration(6000)
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MapboxMap(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    mapState = rememberMapState {
                        gesturesSettings = GesturesSettings {
                            pinchToZoomEnabled = false
                            doubleTapToZoomInEnabled = false
                            quickZoomEnabled = false
                            doubleTouchToZoomOutEnabled = false
                        }
                    },
                    mapViewportState = mapViewportState,
                    style = {
                        MapStyle(style = "mapbox://styles/martindios/cm851rhgo004q01qzbcrg0fb9")
                    }
                ) {
                    val markerResourceId = R.drawable.red_marker
                    val marker = rememberIconImage(key = markerResourceId, painter = painterResource(markerResourceId))
                    PointAnnotation(
                        point = Point.fromLngLat(-8.560296146026845, 42.873506927274846)
                    ) {
                        iconImage = marker
                        interactionsState.onClicked {
                            // Llamamos a la función fetchLocalById para obtener el local con id 1
                            fetchLocalById(1, context) { local ->
                                if (local != null) {
                                    markerLocal.value = local
                                    showMarkerInfo = true
                                }
                            }
                            true
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

                Button(
                    onClick = {
                        mapboxMapRef.value?.flyTo(
                            cameraOptions {
                                center(Point.fromLngLat(-8.560296146026845, 42.873506927274846))
                                zoom(12.5)
                                pitch(75.0)
                                bearing(130.0)
                            },
                            mapAnimationOptions {
                                duration(6000)
                            },
                            animatorListener = object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) { }
                                override fun onAnimationEnd(animation: Animator) {
                                    mapboxMapRef.value?.flyTo(
                                        cameraOptions {
                                            center(Point.fromLngLat(-8.560296146026845, 42.873506927274846))
                                            zoom(12.5)
                                            pitch(0.0)
                                            bearing(0.0)
                                        },
                                        mapAnimationOptions {
                                            duration(3000)
                                        }
                                    )
                                }
                                override fun onAnimationCancel(animation: Animator) { }
                                override fun onAnimationRepeat(animation: Animator) { }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Globe Fly To")
                }

                Button(
                    onClick = { mapViewportState.transitionToFollowPuckState() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = "Center on My Location")
                }
            }

            if (showMarkerInfo && markerLocal.value != null) {
                MarkerInfoSheet(local = markerLocal.value!!, onClose = { showMarkerInfo = false })
            }
        }
    }

    @Composable
    fun MarkerInfoSheet(local: Local, onClose: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(MaterialTheme.colors.surface)
                .padding(top = 64.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Encabezado con el título y la X para cerrar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Información del Lugar", style = MaterialTheme.typography.h6)
                    IconButton(onClick = { onClose() }) { // Se llama a onClose para cerrar la hoja
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }
                Divider()
                // Contenido desplazable con información del Local
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCard(title = "Nombre", description = local.nombre)
                    InfoCard(title = "Dirección", description = local.ubicacion)
                    InfoCard(title = "Categoría", description = local.categoria)
                }
            }
        }
    }


    @Composable
    fun InfoCard(title: String, description: String) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .height(150.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, style = MaterialTheme.typography.subtitle1)
                Text(text = description, style = MaterialTheme.typography.body2)
            }
        }
    }
}