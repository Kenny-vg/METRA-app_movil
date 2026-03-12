package com.softnamic.proyectointegradorii.core.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob

object DataUpdater {
    private const val TAG = "DataUpdater"
    private var job: Job? = null
    private val updaterScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startUpdating() {
        // Si ya hay un job corriendo, no creamos otro
        if (job?.isActive == true) {
            Log.d(TAG, "DataUpdater ya está en marcha.")
            return
        }

        job = updaterScope.launch {
            Log.i(TAG, "🚀 ¡DataUpdater INICIADO GLOBALMENTE!")
            while (isActive) {
                try {
                    if (RestaurantRepository.currentToken.isNotEmpty()) {
                        Log.d(TAG, "🔄 Sincronizando datos con la API...")
                        RestaurantRepository.fetchMesasYZonas()
                        RestaurantRepository.fetchReservaciones()
                    } else {
                        Log.w(TAG, "⚠️ Esperando token de sesión...")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error en el ciclo de actualización: ${e.message}")
                }
                delay(5000) // 5 segundos
            }
        }
    }

    fun stopUpdating() {
        job?.cancel()
        job = null
    }
}
