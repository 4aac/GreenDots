package com.example.impacthon.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.extension.compose.rememberMapState

class MapFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
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
                            // Ajustar longitud y latitud para inicialización del globo
                            center(Point.fromLngLat( -5.5, 28.8))
                            pitch(0.0)
                            bearing(0.0)
                            // Añadir paddin en el top
                            padding(EdgeInsets(1000.0, 0.0, 0.0, 0.0))
                        }
                    }
                )
            }
        }
    }
}