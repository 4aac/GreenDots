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
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class MapFragment : Fragment() {
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                MapScreen()
            }
        }
    }
}

@Composable
fun MapScreen() {
    // Obtenemos el contexto actual para poder iniciar la Activity
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp), // Añade un margen inferior
        verticalArrangement = Arrangement.SpaceBetween // Distribuye los elementos
    ) {
        // Se muestra el MapboxMap ocupando la mayor parte de la pantalla
        MapboxMap(
            modifier = Modifier
                .weight(1f) // Usa un peso menor para permitir espacio para el botón
                .fillMaxSize(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(2.0)
                    center(Point.fromLngLat(-98.0, 39.5))
                    pitch(0.0)
                    bearing(0.0)
                }
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
