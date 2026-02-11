package com.softnamic.proyectointegradorii.data.model

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
    val role: String,
    val cafe_id: Int?
)