package com.example.impacthon.ui.map

import android.animation.Animator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions

class GlobeFlyToActivity : AppCompatActivity() {

    private lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear el MapView y establecerlo como contenido
        val mapView = MapView(this)
        setContentView(mapView)
        mapboxMap = mapView.mapboxMap

        // Configurar la cámara inicial para mostrar el globo en vista amplia
        mapboxMap.setCamera(
            cameraOptions {
                zoom(2.5)
                center(Point.fromLngLat(-5.5, 28.8))
                pitch(0.0)
                bearing(0.0)
            }
        )

        // Cargar el estilo usando el style URI especificado
        mapboxMap.loadStyle("mapbox://styles/martindios/cm851rhgo004q01qzbcrg0fb9") {
            // Una vez cargado el estilo, inicia la animación de flyTo
            mapboxMap.flyTo(
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
                        // Segunda animación: cambiar a vista cenital (pitch y bearing a 0)
                        mapboxMap.flyTo(
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
        }
    }
}
