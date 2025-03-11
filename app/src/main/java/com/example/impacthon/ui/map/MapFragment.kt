package com.example.impacthon.ui.map

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle

class MapFragment : Fragment() {
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent { MapScreen() }
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current

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
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(2.5)
                    center(Point.fromLngLat(-5.5, 28.8))
                    pitch(0.0)
                    bearing(0.0)
                    padding(EdgeInsets(1200.0, 0.0, 0.0, 0.0))
                }
            },
            style = {
                MapStyle(style = "mapbox://styles/martindios/cm851rhgo004q01qzbcrg0fb9")
            }
            )


        // Botón para lanzar GlobeFlyToActivity
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
    }
}
