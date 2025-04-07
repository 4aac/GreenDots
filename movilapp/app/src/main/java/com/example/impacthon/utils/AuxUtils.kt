package com.example.impacthon.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object AuxUtils {
    // Convierte una cadena Base64 a un objeto Bitmap (Imágenes)
    fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            // Remover prefijo si existe (ej. "data:image/jpeg;base64,")
            val pureBase64 = if (base64Str.contains(",")) {
                base64Str.substringAfter(",")
            } else base64Str
            val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Convierte una fecha en formato ISO 8601 a un formato legible
    fun formatDate(dateString: String): String {
        // Definir el formato de entrada
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        // Definir el formato de salida
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault() // Ajustar a la zona horaria local

        return try {
            val date = inputFormat.parse(dateString) // Parsear la fecha
            outputFormat.format(date!!) // Formatear la fecha en el nuevo formato
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // Retornar el string original en caso de error
        }
    }
}