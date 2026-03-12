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

    private fun parseActivo(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt()
            is Boolean -> if (value) 1 else 0
            is String -> value.toDoubleOrNull()?.toInt() ?: 1
            else -> 1 // default
        }
    }

    private val _mesas = MutableStateFlow<List<Mesa>>(emptyList())
    val mesas: StateFlow<List<Mesa>> = _mesas.asStateFlow()

    private val _zonas = MutableStateFlow<List<String>>(listOf("Todas"))
    val zonas: StateFlow<List<String>> = _zonas.asStateFlow()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas.asStateFlow()

    @Volatile
    var currentToken: String = ""

    suspend fun fetchMesasYZonas() {
        if (currentToken.isEmpty()) {
            Log.e(TAG, "❌ TOKEN VACÍO - No se puede consultar la API")
            return
        }
        
        val authHeader = "Bearer $currentToken"

        try {
            // 1. ZONAS
            val resZonas = RetrofitClient.instance.getZonas(authHeader)
            if (resZonas.isSuccessful) {
                val zonasApi = resZonas.body()?.data ?: emptyList()
                val listaNombres = mutableListOf("Todas")
                zonasApi.forEach { 
                    val isActivo = parseActivo(it.activo) == 1 && !it.nombre_zona.contains("(SUSPENDIDA)", ignoreCase = true)
                    val nombreLimpio = it.nombre_zona.replace(" (SUSPENDIDA)", "")
                    val nombre = if (isActivo) nombreLimpio else "$nombreLimpio (SUSPENDIDA)"
                    listaNombres.add(nombre)
                }
                
                if (_zonas.value != listaNombres) {
                    _zonas.value = ArrayList(listaNombres)
                    Log.i(TAG, "✅ Zonas actualizadas: ${listaNombres.size}")
                }
            } else {
                Log.e(TAG, "❌ Error API Zonas: ${resZonas.code()} - ${resZonas.message()}")
            }

            // 2. MESAS
            val resMesas = RetrofitClient.instance.getMesas(authHeader)
            if (resMesas.isSuccessful) {
                val mesasApi = resMesas.body()?.data ?: emptyList()
                val nuevasMesas = mesasApi.map { m ->
                    val zonaActiva = parseActivo(m.zona.activo) == 1 && !m.zona.nombre_zona.contains("(SUSPENDIDA)", ignoreCase = true)
                    val mesaActiva = parseActivo(m.activo) == 1
                    val activa = (mesaActiva && zonaActiva)
                    
                    Log.d(TAG, "Mesa MAP: num=${m.numero_mesa}, activoObj=${m.activo}, mesaActiva=${mesaActiva}, zonaActivaObj=${m.zona.activo}, zonaActiva=${zonaActiva}")
                    
                    val nombreZonaLimpio = m.zona.nombre_zona.replace(" (SUSPENDIDA)", "")
                    Mesa(
                        id = m.id,
                        nombre = "Mesa ${m.numero_mesa}",
                        capacidad = m.capacidad,
                        zona = if (zonaActiva) nombreZonaLimpio else "$nombreZonaLimpio (SUSPENDIDA)",
                        estado = EstadoMesa.DISPONIBLE,
                        activo = if (activa) 1 else 0
                    )
                }
                _mesas.value = ArrayList(nuevasMesas)
                Log.d(TAG, "✅ Mesas sincronizadas: ${nuevasMesas.size}")
            } else {
                Log.e(TAG, "❌ Error API Mesas: ${resMesas.code()} - ${resMesas.message()} - ${resMesas.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR FATAL RED: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun fetchReservaciones() {
        _reservas.value = DataMock.reservas.toList()
    }
}
