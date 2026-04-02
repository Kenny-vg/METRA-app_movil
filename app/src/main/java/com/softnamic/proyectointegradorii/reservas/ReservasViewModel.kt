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

    private val _filtroTabs = MutableStateFlow("Hoy")
    val filtroTabs: StateFlow<String> = _filtroTabs

    val reservasFiltradas: StateFlow<List<Reserva>> = combine(todasLasReservas, _busqueda, _filtroTabs) { reservas, query, tab ->
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale("es", "MX"))
        sdf.timeZone = java.util.TimeZone.getTimeZone("America/Mexico_City")
        val hoy = sdf.format(java.util.Date())

        val filtradasPorTab = if (tab == "Hoy") {
            reservas.filter { it.fecha == hoy }
        } else {
            reservas.filter { it.fecha > hoy }
        }

        val ordenadas = filtradasPorTab.sortedWith(compareBy { 
            it.estado?.lowercase() == "finalizada" || 
            it.estado?.lowercase() == "cancelada" || 
            it.estado?.lowercase() == "no_show" 
        })

        if (query.isBlank()) {
            ordenadas
        } else {
            ordenadas.filter { reserva ->
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

    fun seleccionarTab(tab: String) {
        _filtroTabs.value = tab
    }

    fun checkinReservacion(idReserva: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, msg) = RestaurantRepository.checkinReservacion(idReserva)
            onResult(exito, msg)
        }
    }

    fun abrirMesa(idReserva: Int, mesaIds: List<Int>, zonaId: Int, numPersonas: Int, comentarios: String?, nombreCliente: String?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, mensaje) = RestaurantRepository.abrirMesa(idReserva, mesaIds, zonaId, numPersonas, comentarios, nombreCliente)
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
