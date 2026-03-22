package com.softnamic.proyectointegradorii.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservasViewModel : ViewModel() {

    val reservas: StateFlow<List<Reserva>> = RestaurantRepository.reservas
    val mesas: StateFlow<List<com.softnamic.proyectointegradorii.mesas.Mesa>> = RestaurantRepository.mesas

    fun checkinReservacion(idReserva: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, msg) = RestaurantRepository.checkinReservacion(idReserva)
            onResult(exito, msg)
        }
    }

    fun abrirMesa(idReserva: Int, idMesa: Int, zonaId: Int, numPersonas: Int, comentarios: String?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, mensaje) = RestaurantRepository.abrirMesa(idReserva, idMesa, zonaId, numPersonas, comentarios)
            onResult(exito, mensaje)
        }
    }

    fun cancelarReservacion(idReserva: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, mensaje) = RestaurantRepository.cancelarReservacion(idReserva)
            onResult(exito, mensaje)
        }
    }
}
