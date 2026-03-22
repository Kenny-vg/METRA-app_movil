package com.softnamic.proyectointegradorii.reservas

data class Cafeteria(
    val nombre: String
)

data class ReservaResponse(
    val id: Int,
    val folio: String,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String,
    val numero_personas: Int,
    val nombre_cliente: String?,
    val estado: String,
    val comentarios: String?,
    val cafeteria: Cafeteria? = null,
    val zona: com.google.gson.JsonElement? = null,
    val promocion: com.google.gson.JsonElement? = null,
    val ocasion: com.google.gson.JsonElement? = null
)
