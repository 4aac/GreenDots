package com.example.impacthon.ui.map

import android.Manifest
import android.animation.Animator
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
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

class MapFragment : Fragment() {
    private var permissionsGranted by mutableStateOf(false)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el launcher para solicitar permisos de ubicación
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                // Se determina si al menos uno de los permisos fue concedido
                val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                onPermissionResult(granted)
            }

        // Verifica si ya se han concedido los permisos de ubicación
        permissionsGranted = PermissionsManager.areLocationPermissionsGranted(requireContext())
        if (!permissionsGranted) {
            // Solicita los permisos
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

    // Método invocado desde el callback del launcher de permisos
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
        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.5)
                center(Point.fromLngLat(-5.5, 28.8))
                pitch(0.0)
                bearing(0.0)
                padding(EdgeInsets(1200.0, 0.0, 0.0, 0.0))
            }
        }
        // Estado para guardar la referencia al MapboxMap
        val mapboxMapRef = remember { mutableStateOf<MapboxMap?>(null) }

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
                // MapEffect nos permite interactuar con el MapView subyacente
                MapEffect(Unit) { mapView ->
                    // Guarda la referencia del MapboxMap para usarla luego
                    mapboxMapRef.value = mapView.mapboxMap

                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearing = PuckBearing.HEADING
                        puckBearingEnabled = true
                        enabled = true
                    }
                    // Hace que la cámara siga la ubicación del usuario
                    mapViewportState.transitionToFollowPuckState()
                }
            }

            // Botón para disparar la animación flyTo en el mismo mapa
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
                            duration(12_000)
                        },
                        animatorListener = object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                // Opcional: acción al iniciar la animación
                            }
                            override fun onAnimationEnd(animation: Animator) {
                                // Segunda animación: vuelve a una vista cenital (pitch y bearing a 0)
                                mapboxMapRef.value?.flyTo(
                                    cameraOptions {
                                        center(Point.fromLngLat(-8.560296146026845, 42.873506927274846))
                                        zoom(12.5)
                                        pitch(0.0)
                                        bearing(0.0)
                                    },
                                    mapAnimationOptions {
                                        duration(4_000)
                                    }
                                )
                            }
                            override fun onAnimationCancel(animation: Animator) {
                                // Opcional: acción si se cancela la animación
                            }
                            override fun onAnimationRepeat(animation: Animator) {
                                // Opcional: acción si se repite la animación
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Globe Fly To")
            }

            // Botón opcional para recenter en la ubicación del usuario
            Button(
                onClick = { mapViewportState.transitionToFollowPuckState() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Center on My Location")
            }
        }
    }
}
