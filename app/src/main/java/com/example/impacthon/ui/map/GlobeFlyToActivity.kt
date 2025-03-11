package com.example.impacthon.ui.map

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.example.impacthon.R

class GlobeFlyToActivity : AppCompatActivity() {

    private lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear el MapView y establecerlo como contenido
        val mapView = MapView(this)
        setContentView(mapView)
        mapboxMap = mapView.getMapboxMap()

        // Configurar la cámara inicial para mostrar el globo en vista amplia
        mapboxMap.setCamera(
            cameraOptions {
                center(Point.fromLngLat(0.0, 0.0)) // Centro general del globo
                zoom(1.0)                        // Zoom bajo para ver el globo completo
                pitch(0.0)
                bearing(0.0)
            }
        )

        // Cargar el estilo con proyección Globe y efectos 3D
        mapboxMap.loadStyle(
            style(Style.SATELLITE_STREETS) {
                +projection(ProjectionName.GLOBE)
                +atmosphere {
                    color(Color.rgb(220, 159, 159))
                    highColor(Color.rgb(220, 159, 159))
                    horizonBlend(0.4)
                }
                +rasterDemSource("raster-dem") {
                    url("mapbox://mapbox.terrain-rgb")
                }
                +terrain("raster-dem")
            }
        ) {
            // Una vez cargado el estilo, se inicia la animación flyTo hacia las coordenadas deseadas
            mapboxMap.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(-8.560296146026845, 42.873506927274846)) // Coordenadas destino
                    zoom(12.5)    // Nivel de zoom para acercar la vista
                    pitch(75.0)   // Ángulo de inclinación para efecto 3D
                    bearing(130.0) // Rotación de la cámara
                },
                mapAnimationOptions {
                    duration(12_000) // Duración de la animación (12 segundos)
                }
            )
        }
    }
}
