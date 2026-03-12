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

    private const val INTERVALO_MS = 5000L 

    private val updaterScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startUpdating() {
        if (job?.isActive == true) {
            Log.d(TAG, "DataUpdater ya está corriendo.")
            return
        }

        Log.d(TAG, "Iniciando DataUpdater...")

        job = updaterScope.launch {
            while (isActive) {
                try {
                    if (RestaurantRepository.currentToken.isNotEmpty()) {
                        Log.d(TAG, "Ejecutando actualización de red...")
                        RestaurantRepository.fetchMesasYZonas()
                        RestaurantRepository.fetchReservaciones()
                    } else {
                        Log.w(TAG, "Esperando token para actualizar...")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error en el loop de actualización: ${e.message}")
                }
                delay(INTERVALO_MS)
            }
        }
    }

    fun stopUpdating() {
        job?.cancel()
        job = null
        Log.d(TAG, "DataUpdater detenido.")
    }
}
