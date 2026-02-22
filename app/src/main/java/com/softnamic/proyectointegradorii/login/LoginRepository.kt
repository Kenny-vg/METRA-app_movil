package com.softnamic.proyectointegradorii.login

import com.softnamic.proyectointegradorii.core.network.RetrofitClient
import retrofit2.Response

/**
 * Repositorio que maneja las operaciones de autenticación.
 * Su única responsabilidad es obtener los datos de la fuente de datos (en este caso, la red).
 */
class LoginRepository {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return RetrofitClient.instance.login(email, password)
    }
}