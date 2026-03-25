package com.softnamic.proyectointegradorii.reservas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReservasViewModel : ViewModel() {

    private val todasLasReservas = RestaurantRepository.reservas
    val mesas: StateFlow<List<com.softnamic.proyectointegradorii.mesas.Mesa>> = RestaurantRepository.mesas

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    val reservasFiltradas: StateFlow<List<Reserva>> = combine(todasLasReservas, _busqueda) { reservas, query ->
        if (query.isBlank()) {
            reservas
        } else {
            reservas.filter { reserva ->
                val nombreMatch = reserva.nombreCliente?.contains(query, ignoreCase = true) ?: false
                val folioMatch = reserva.folio.contains(query, ignoreCase = true)
                nombreMatch || folioMatch
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun buscar(query: String) {
        _busqueda.value = query
    }

    fun checkinReservacion(idReserva: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, msg) = RestaurantRepository.checkinReservacion(idReserva)
            onResult(exito, msg)
        }
    }

    fun abrirMesa(idReserva: Int, idMesa: Int, zonaId: Int, numPersonas: Int, comentarios: String?, nombreCliente: String?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, mensaje) = RestaurantRepository.abrirMesa(idReserva, idMesa, zonaId, numPersonas, comentarios, nombreCliente)
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
