package com.softnamic.proyectointegradorii.login

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data?
)

data class Data(
    val token: String,
    val usuario: Usuario
)

data class Usuario(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val cafe_id: Int?,
    val nombre_cafeteria: String?, // Corregido según la API real
    val dias_restantes: Int?,
    val fecha_fin_suscripcion: String?
)
