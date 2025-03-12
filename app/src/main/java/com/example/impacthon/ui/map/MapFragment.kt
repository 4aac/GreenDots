package com.example.impacthon.ui.map

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

class MapFragment : Fragment(), PermissionsListener {
    private lateinit var permissionsManager: PermissionsManager
    private var permissionsGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if location permissions are already granted
        permissionsGranted = PermissionsManager.areLocationPermissionsGranted(requireContext())
        if (!permissionsGranted) {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // PermissionsListener implementation
    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        // This is called if the user needs an explanation about the permission
        Toast.makeText(
            requireContext(),
            "This app needs location permissions to show your location on the map",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        permissionsGranted = granted
        if (granted) {
            // Refresh the UI now that we have permissions
            view?.findViewById<ComposeView>(android.R.id.content)?.setContent {
                MapScreenWithPermissions(true)
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Location permissions were denied. Some features will be disabled.",
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
                Text("Location permissions are required to show your location on the map")
                Button(
                    onClick = {
                        permissionsManager = PermissionsManager(this@MapFragment)
                        permissionsManager.requestLocationPermissions(requireActivity())
                    }
                ) {
                    Text("Request Permissions")
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
                // Configure location component when permissions are granted
                MapEffect(Unit) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        puckBearing = PuckBearing.HEADING
                        puckBearingEnabled = true
                        enabled = true
                    }
                    // Make camera follow the user's location
                    mapViewportState.transitionToFollowPuckState()
                }
            }

            // Button to launch GlobeFlyToActivity
            Button(
                onClick = {
                    val intent = Intent(context, GlobeFlyToActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Abrir GlobeFlyTo")
            }

            // Optional: Add a button to recenter on user's location
            Button(
                onClick = {
                    mapViewportState.transitionToFollowPuckState()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "Center on My Location")
            }
        }
    }
}