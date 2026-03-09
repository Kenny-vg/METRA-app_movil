package com.softnamic.proyectointegradorii.core.network

import com.softnamic.proyectointegradorii.login.LoginResponse
import com.softnamic.proyectointegradorii.mesas.MesaResponse
import com.softnamic.proyectointegradorii.mesas.ZonaResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    // Probamos con gerente de nuevo, ya que staff dio 404
    @GET("api/gerente/mesas")
    suspend fun getMesas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<MesaResponse>>

    @GET("api/gerente/zonas")
    suspend fun getZonas(
        @Header("Authorization") token: String
    ): Response<ApiResponse<ZonaResponse>>
}
