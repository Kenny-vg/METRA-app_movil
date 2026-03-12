package com.softnamic.proyectointegradorii.core.data

import android.util.Log
import com.softnamic.proyectointegradorii.core.network.RetrofitClient
import com.softnamic.proyectointegradorii.core.pruebas.DataMock
import com.softnamic.proyectointegradorii.mesas.EstadoMesa
import com.softnamic.proyectointegradorii.mesas.Mesa
import com.softnamic.proyectointegradorii.reservas.Reserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RestaurantRepository {
    private const val TAG = "RestaurantRepo"

    private val _mesas = MutableStateFlow<List<Mesa>>(emptyList())
    val mesas: StateFlow<List<Mesa>> = _mesas.asStateFlow()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    private val _zonas = MutableStateFlow<List<String>>(listOf("Todas"))
    val zonas: StateFlow<List<String>> = _zonas.asStateFlow()

    // @Volatile asegura que el DataUpdater vea el token actualizado al instante
    @Volatile
    var currentToken: String = ""

    suspend fun fetchMesasYZonas() {
        if (currentToken.isEmpty()) {
            Log.e(TAG, "¡ALERTA!: Intentando actualizar sin token. Abortando.")
            return
        }
        
        val authHeader = "Bearer $currentToken"
        Log.d(TAG, "Iniciando descarga de datos de la API...")

        try {
            // 1. Zonas
            val resZonas = RetrofitClient.instance.getZonas(authHeader)
            if (resZonas.isSuccessful) {
                val zonasApi = resZonas.body()?.data ?: emptyList()
                val zonasActivas = zonasApi.filter { it.activo == 1 }.map { it.nombre_zona }
                
                val nuevasZonas = mutableListOf("Todas")
                nuevasZonas.addAll(zonasActivas)

                if (_zonas.value != nuevasZonas) {
                    _zonas.value = nuevasZonas
                    Log.i(TAG, "Zonas actualizadas en tiempo real: $nuevasZonas")
                }
            }

            // 2. Mesas
            val resMesas = RetrofitClient.instance.getMesas(authHeader)
            if (resMesas.isSuccessful) {
                val mesasApi = resMesas.body()?.data ?: emptyList()
                val nuevasMesas = mesasApi.map {
                    val isActivo = if (it.activo == 1 && it.zona.activo == 1) 1 else 0
                    Mesa(it.id, "Mesa ${it.numero_mesa}", it.capacidad, it.zona.nombre_zona, EstadoMesa.DISPONIBLE, isActivo)
                }
                _mesas.value = nuevasMesas
                Log.d(TAG, "Mesas actualizadas en tiempo real: ${nuevasMesas.size}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fatal de red: ${e.message}")
        }
    }

    suspend fun fetchReservaciones() {
        _reservas.value = DataMock.reservas.toList()
    }
}
