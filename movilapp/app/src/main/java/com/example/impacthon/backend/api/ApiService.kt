package com.example.impacthon.backend

import com.example.impacthon.backend.models.Usuario
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Crear un usuario
    @POST("/user/new")
    fun createUser(@Body usuario: Usuario): Call<String>

    // Obtener un usuario por nickname
    @GET("/user/get/{nickname}")
    fun getUser(@Path("nickname") nickname: String): Call<Usuario>

    // Obtener todos los usuarios
    @GET("/user/getall")
    fun getAllUsers(): Call<List<Usuario>>

    // Eliminar un usuario por nickname
    @DELETE("/user/delete/{nickname}")
    fun deleteUser(@Path("nickname") nickname: String): Call<Void>
}