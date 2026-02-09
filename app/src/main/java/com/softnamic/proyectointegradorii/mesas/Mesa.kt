package com.softnamic.proyectointegradorii.mesas

data class Mesa(
    val id: Int,
    val nombre: String,
    val capacidad: Int,
    val zona: String,
    var estado: EstadoMesa
)

enum class EstadoMesa {
    DISPONIBLE,
    OCUPADA
}
