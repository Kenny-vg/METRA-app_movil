package com.softnamic.proyectointegradorii.reservas

data class Reserva(
    val id: Int,
    val folio: String,
    val nombre: String, // Usado para Cafetería
    val fecha: String,
    val hora: String, // Combinación de hora inicio - fin
    val personas: Int,
    val nombreCliente: String?,
    val zona: String, // Por defecto General si no viene en API
    val zonaId: Int = 0, // ID numérico de la zona para enviar al backend
    val comentarios: String?,
    var mesa: String?,
    var promocion: String = "Sin promoción",
    var precioPromocion: String = "",
    var ocasion: String = "Sin ocasión",
    var estado: String? = null // "Pendiente", "Llegó", "No llegó", etc.
)
