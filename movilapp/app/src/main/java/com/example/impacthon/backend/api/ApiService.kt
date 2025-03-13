package com.example.impacthon.backend.api

import com.example.impacthon.backend.models.Local
import com.example.impacthon.backend.models.Usuario
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import com.example.impacthon.backend.models.Opinion
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Endpoints para Usuarios
    @POST("/user/new")
    fun createUser(@Body usuario: Usuario): Call<String>

    @GET("/user/get/{nickname}")
    fun getUser(@Path("nickname") nickname: String): Call<Usuario>

    @GET("/user/getall")
    fun getAllUsers(): Call<List<Usuario>>

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

    // Endpoints para Locales
    @GET("/local/get/{id}")
    fun getLocal(@Path("id") id: Int): Call<Local>

    @GET("/local/getall")
    fun getAllLocales(): Call<List<Local>>

    @POST("/local/new")
    fun createLocal(@Body local: Local): Call<String>

    @DELETE("/local/delete/{id}")
    fun deleteLocal(@Path("id") id: Int): Call<String>

    // Endpoints para Opiniones
    @POST("/opiniones/crear")
    fun createOpinion(@Body opinion: Opinion): Call<String>

    @GET("/opiniones/get/local/{id}")
    fun getOpinionesPorLocal(@Path("id") id: Int): Call<List<Opinion>>
}
