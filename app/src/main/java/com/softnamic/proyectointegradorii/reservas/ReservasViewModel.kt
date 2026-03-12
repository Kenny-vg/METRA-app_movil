package com.softnamic.proyectointegradorii.reservas

import androidx.lifecycle.ViewModel
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import kotlinx.coroutines.flow.StateFlow

class ReservasViewModel : ViewModel() {

    // Recuperamos el StateFlow desde el Repositorio
    val reservas: StateFlow<List<Reserva>> = RestaurantRepository.reservas

}
