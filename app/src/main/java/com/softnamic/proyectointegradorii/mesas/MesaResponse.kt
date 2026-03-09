package com.softnamic.proyectointegradorii.mesas

data class MesaResponse(
    val id: Int,
    val numero_mesa: Int,
    val capacidad: Int,
    val zona: ZonaResponse
)