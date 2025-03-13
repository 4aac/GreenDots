package com.example.impacthon.backend.api

import com.example.impacthon.backend.models.Usuario
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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

    // Obtener foto de usuario
    @GET("/user/image/{nickname}")
    fun getUserImage(@Path("nickname") nickname: String): Call<ResponseBody>

    // Subir foto de usuario
    @Multipart
    @POST("/user/upload/{nickname}")
    fun uploadUserImage(
        @Path("nickname") nickname: String,
        @Part imagen: MultipartBody.Part
    ): Call<String>
}
