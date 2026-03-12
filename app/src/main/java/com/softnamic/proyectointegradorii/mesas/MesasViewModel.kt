package com.softnamic.proyectointegradorii.mesas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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

    // Filtro seleccionado
    private val _zonaSeleccionada = MutableStateFlow("Todas")
    val zonaSeleccionada: StateFlow<String> = _zonaSeleccionada

    // Mesas filtradas
    val mesasFiltradas: StateFlow<List<Mesa>> = combine(todasLasMesas, _zonaSeleccionada) { mesas, zona ->
        if (zona == "Todas") mesas else mesas.filter { it.zona == zona }
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
}
