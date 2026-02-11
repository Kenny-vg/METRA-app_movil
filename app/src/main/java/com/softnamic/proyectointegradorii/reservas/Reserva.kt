package com.softnamic.proyectointegradorii.reservas

data class Reserva(
    val nombre: String,
    val hora: String,
    val personas: Int,
    val zona: String,
    val comentarios: String?,
    var mesa: String?,
    var estado: String? = null // Puede ser "Llegó", "No llegó", etc.
)
