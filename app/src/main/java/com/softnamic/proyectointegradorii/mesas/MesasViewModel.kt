package com.softnamic.proyectointegradorii.mesas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MesasViewModel : ViewModel() {

    // Flujos del Repositorio
    private val todasLasMesas = RestaurantRepository.mesas
    
    // Zonas convertidas en un StateFlow que se mantiene vivo
    val zonas: StateFlow<List<String>> = RestaurantRepository.zonas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf("Todas")
        )

    // Filtros
    private val _zonaSeleccionada = MutableStateFlow("Todas")
    val zonaSeleccionada: StateFlow<String> = _zonaSeleccionada

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    // Mesas filtradas por ZONA y por BÚSQUEDA (Número de mesa)
    val mesasFiltradas: StateFlow<List<Mesa>> = combine(todasLasMesas, _zonaSeleccionada, _busqueda) { mesas, zona, query ->
        var filtradas = mesas

        // 1. Filtro por Zona
        if (zona != "Todas") {
            val zonaLimpia = zona.replace(" (SUSPENDIDA)", "")
            filtradas = filtradas.filter { it.zona.replace(" (SUSPENDIDA)", "") == zonaLimpia }
        }

        // 2. Filtro por Búsqueda (Número/Nombre de mesa o Zona)
        if (query.isNotBlank()) {
            filtradas = filtradas.filter { 
                it.nombre.contains(query, ignoreCase = true) || 
                it.zona.contains(query, ignoreCase = true)
            }
        }

        filtradas
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun seleccionarZona(zona: String) {
        if (_zonaSeleccionada.value != zona) {
            _zonaSeleccionada.value = zona
        }
    }

    fun buscar(query: String) {
        _busqueda.value = query
    }

    fun finalizarOcupacion(idOcupacion: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (exito, mensaje) = RestaurantRepository.finalizarOcupacion(idOcupacion)
            onResult(exito, mensaje)
        }
    }
}
