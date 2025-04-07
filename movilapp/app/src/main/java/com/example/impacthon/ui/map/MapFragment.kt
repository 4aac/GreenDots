package com.example.impacthon.ui.map

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.impacthon.R
import com.example.impacthon.backend.models.Local
import com.example.impacthon.ui.ViewModelFactory
import com.example.impacthon.utils.MapUtils
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.delay

class MapFragment : Fragment() {
    // Controla el estado de permisos de localización
    private var permissionsGranted by mutableStateOf(false)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configuración del launcher para pedir permisos al usuario
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                onPermissionResult(granted)
            }
        // Comprueba si los permisos ya están concedidos
        permissionsGranted = PermissionsManager.areLocationPermissionsGranted(requireContext())
        if (!permissionsGranted) {
            // Solicitar permisos de localización
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapViewModel = ViewModelProvider(this, ViewModelFactory(requireContext())).get(MapViewModel::class.java)

        // Retorna una vista Compose que muestra la pantalla del mapa con permisos
        return ComposeView(requireContext()).apply {
            setContent { MapScreenWithPermissions(permissionsGranted) }
        }
    }

    // Actualiza el estado de permisos y la UI según el resultado
    private fun onPermissionResult(granted: Boolean) {
        permissionsGranted = granted
        if (granted) {
            view?.findViewById<ComposeView>(android.R.id.content)?.setContent { MapScreenWithPermissions(true) }
        } else {
            Toast.makeText(
                requireContext(),
                R.string.permision_denied,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Muestra la pantalla del mapa o la solicitud de permisos según el estado
    @Composable
    private fun MapScreenWithPermissions(permissionsGranted: Boolean) {
        if (permissionsGranted) {
            MapScreen()
        } else {
            PermissionRequestScreen()
        }
    }

    // Pantalla que solicita al usuario activar los permisos de localización
    @Composable
    private fun PermissionRequestScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.text_no_permisions),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(R.color.green500),
                        contentColor = colorResource(R.color.white)
                    )
                ) { Text(stringResource(id = R.string.permision_button)) }
            }
        }
    }

    // Pantalla principal del mapa con lógica de interacción y animaciones
    @Composable
    private fun MapScreen() {
        val context = LocalContext.current

        // Estados para controlar la visualización de información, opiniones y lista de locales
        var showMarkerInfo by remember { mutableStateOf(false) }
        val markerLocal = remember { mutableStateOf<Local?>(null) }
        var allLocales by remember { mutableStateOf<List<Local>>(emptyList()) }
        var showNewOpinionForm by remember { mutableStateOf(false) }
        var resolvedAddress by remember { mutableStateOf<String?>(null) }

        // Carga asíncrona de locales desde el backend
        LaunchedEffect(Unit) {
            MapUtils.fetchAllLocales(context) { locales ->
                locales?.let { allLocales = it }
            }
        }

        // Configura el estado de la cámara del mapa
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

        // Ejecuta la animación inicial de la cámara si aún no se ha realizado
        LaunchedEffect(mapboxMapRef.value) {
            if (mapboxMapRef.value != null && !animationStarted && !mapViewModel.initialAnimationDone) {
                mapViewModel.initialAnimationDone = true
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
                // Configuración del mapa utilizando Compose y Mapbox
                MapboxMap(
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
                    scaleBar = {},
                    mapViewportState = mapViewportState,
                    style = { MapStyle(style = "mapbox://styles/martindios/cm851rhgo004q01qzbcrg0fb9") }
                ) {
                    // Configura el marcador con el recurso definido
                    val markerResourceId = R.drawable.red_marker
                    val markerIcon = rememberIconImage(key = markerResourceId, painter = painterResource(markerResourceId))
                    // Agrega un marcador para cada local obtenido
                    allLocales.forEach { local ->
                        MapUtils.parseLocation(local.ubicacion)?.let { (lat, lng) ->
                            PointAnnotation(
                                point = Point.fromLngLat(lng, lat)
                            ) {
                                iconImage = markerIcon
                                // Al pulsar sobre el marcador, se muestran los detalles e inicia la petición para la dirección
                                interactionsState.onClicked {
                                    markerLocal.value = local
                                    showMarkerInfo = true
                                    resolvedAddress = local.ubicacion
                                    //val accessToken = ""
                                    val accessToken = context.getString(R.string.mapbox_access_token)
                                    MapUtils.fetchAddressFromCoordinates(lat, lng, accessToken) { address ->
                                        address?.let {
                                            resolvedAddress = it
                                        }
                                    }
                                    true
                                }
                            }
                        }
                    }
                    // Inicializa la configuración del componente de ubicación en el mapa
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
            // Botón para centrar el mapa en la ubicación del usuario
            FloatingActionButton(
                onClick = {
                    mapViewportState.transitionToFollowPuckState()
                },
                backgroundColor = colorResource(id = R.color.green500),
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
            // Barra de búsqueda para seleccionar ciudad
            MapComponents().SearchBarDropdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = 50.dp),
                onCitySelected = { city ->

                    // Evita animación si la ciudad ya está seleccionada
                    if(mapViewModel.selectedCity == city) return@SearchBarDropdown

                    mapViewModel.selectedCity = city

                    // Define las coordenadas para cada ciudad
                    val (lng, lat) = when (city) {
                        "Vigo" -> Pair(-8.720, 42.240)
                        "Pontevedra" -> Pair(-8.640, 42.431)
                        "A Coruña" -> Pair(-8.406, 43.362)
                        "Santiago de Compostela" -> Pair(-8.546, 42.878)
                        else -> Pair(-3.74922, 40.463667)
                    }
                    // Anima la cámara hacia la ciudad seleccionada
                    MapUtils.animateFlyTo(mapboxMapRef, lng, lat)
                }
            )

            // Muestra la hoja de información del marcador
            if (showMarkerInfo && markerLocal.value != null) {
                MapComponents().MarkerInfoSheet(
                    local = markerLocal.value!!,
                    resolvedAddress = resolvedAddress,
                    onClose = { showMarkerInfo = false },
                    onAddOpinion = { showNewOpinionForm = true }
                )
            }
        }

        // Muestra el formulario para añadir opinión si se activa y existe un local seleccionado
        if (showNewOpinionForm && markerLocal.value != null) {
            if(mapViewModel.nicknameUsuario() == null){
                Toast.makeText(
                    context,
                    R.string.text_must_loggin,
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                MapComponents().NewOpinionFormDialog(mapViewModel = mapViewModel,local = markerLocal.value!!, onDismiss = { showNewOpinionForm = false })
            }
        }
    }

}