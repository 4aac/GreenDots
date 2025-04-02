package com.example.impacthon.ui.map

import android.animation.Animator
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.example.impacthon.backend.api.RetrofitClient
import com.example.impacthon.backend.models.Local
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapUtils {
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

    // Función para obtener todos los locales usando Retrofit.
    fun fetchAllLocales(context: Context, onResult: (List<Local>?) -> Unit) {
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

    // Función auxiliar para parsear la cadena de ubicación al par (latitud, longitud)
    fun parseLocation(location: String): Pair<Double, Double>? {
        val parts = location.split(",")
        return if (parts.size == 2) {
            val lat = parts[0].trim().toDoubleOrNull()
            val lng = parts[1].trim().toDoubleOrNull()
            if (lat != null && lng != null) Pair(lat, lng) else null
        } else null
    }

}