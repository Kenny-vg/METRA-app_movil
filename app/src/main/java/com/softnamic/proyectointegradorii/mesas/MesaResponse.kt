package com.softnamic.proyectointegradorii.mesas

data class MesaResponse(
    val id: Int,
    val numero_mesa: Int,
    val capacidad: Int,
    val activo: Any? = null,
    val zona: ZonaResponse
)

data class OcupacionResponse(
    val id: Int,
    val mesa_id: Int,
    val estado: String
)

data class EstadoMesaResponse(
    val id: Int,
    val numero: Int? = null,
    val numero_mesa: Int? = null,
    val capacidad: Int,
    val estado: String,
    val ocupacion_id: Int? = null
)