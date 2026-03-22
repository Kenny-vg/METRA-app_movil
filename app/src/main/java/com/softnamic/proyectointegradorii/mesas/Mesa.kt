package com.softnamic.proyectointegradorii.mesas

data class Mesa(
    val id: Int,
    val nombre: String,
    val capacidad: Int,
    val zona: String,
    val zonaId: Int = 0,
    var estado: EstadoMesa,
    val activo: Int = 1,
    var ocupacionId: Int? = null
)

enum class EstadoMesa {
    DISPONIBLE,
    OCUPADA,
    RESERVADA
}
