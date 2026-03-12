package com.softnamic.proyectointegradorii.mesas

data class ZonaResponse(
    val id: Int,
    val nombre_zona: String,
    val activo: Int? = 1 // Optional for backwards compatibility, defaults to 1
)
