package com.softnamic.proyectointegradorii

import com.softnamic.proyectointegradorii.reservas.Reserva

object DataMock {
    val reservas = mutableListOf(
        Reserva(
            nombre = "María López",
            hora = "12:30 pm",
            personas = 4,
            zona = "Fumadores",
            comentarios = "Mesa cerca de la ventana",
            mesa = "Mesa 3"
        ),
        Reserva(
            nombre = "Juan Pérez",
            hora = "1:00 pm",
            personas = 2,
            zona = "No fumadores",
            comentarios = "Cumpleaños",
            mesa = null
        ),
        Reserva(
            nombre = "Ana Torres",
            hora = "2:15 pm",
            personas = 5,
            zona = "Terraza",
            comentarios = "Silla para bebé",
            mesa = "Mesa 5"
        )
    )
}
