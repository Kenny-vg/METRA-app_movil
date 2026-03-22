package com.softnamic.proyectointegradorii.core.network

import com.softnamic.proyectointegradorii.login.LoginResponse
import com.softnamic.proyectointegradorii.mesas.MesaResponse
import com.softnamic.proyectointegradorii.mesas.ZonaResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("api/staff/mesas")
    suspend fun getMesas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<MesaResponse>>

    @GET("api/staff/zonas")
    suspend fun getZonas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<ZonaResponse>>

    @Headers("Cache-Control: no-cache", "Pragma: no-cache")
    @GET("api/{rol}/reservaciones")
    suspend fun getReservaciones(
        @Header("Authorization") token: String,
        @Path("rol") rol: String,
        @Query("fecha") fecha: String
    ): Response<ApiResponse<com.softnamic.proyectointegradorii.reservas.ReservaResponse>>

    @PATCH("api/staff/reservaciones/{id}/checkin")
    suspend fun completarReservacion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @POST("api/staff/ocupaciones")
    suspend fun abrirMesa(
        @Header("Authorization") token: String,
        @Body request: AbrirMesaRequest
    ): Response<ResponseBody>

    @GET("api/staff/ocupaciones")
    suspend fun getOcupaciones(
        @Header("Authorization") token: String
    ): Response<ApiResponse<com.softnamic.proyectointegradorii.mesas.OcupacionResponse>>

    @GET("api/staff/mesas-estado")
    suspend fun getEstadoMesas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<com.softnamic.proyectointegradorii.mesas.EstadoMesaResponse>>

    @PATCH("api/staff/ocupaciones/{id}/finalizar")
    suspend fun finalizarOcupacion(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @PATCH("api/{rol}/reservaciones/{id}/cancelar")
    suspend fun cancelarReservacion(
        @Header("Authorization") token: String,
        @Path("rol") rol: String,
        @Path("id") id: Int
    ): Response<ResponseBody>
}

data class AbrirMesaRequest(
    val mesa_id: Int,
    val zona_id: Int,
    val reservacion_id: Int? = null,
    val numero_personas: Int? = null,
    val nombre_cliente: String? = null,
    val email: String? = null,
    val tipo: String = "reservacion",
    val comentarios: String? = null
)
