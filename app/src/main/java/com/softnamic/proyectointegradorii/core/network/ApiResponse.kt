package com.softnamic.proyectointegradorii.core.network

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: List<T>
)
