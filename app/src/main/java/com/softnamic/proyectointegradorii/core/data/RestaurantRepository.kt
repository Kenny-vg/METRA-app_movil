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

    @Volatile
    var currentRole: String = "staff"

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

            // 3. ESTADO REAL DE MESAS (endpoint mesas-estado)
            val resEstado = RetrofitClient.instance.getEstadoMesas(authHeader)
            val estadoMesasMap = if (resEstado.isSuccessful) {
                val lista = resEstado.body()?.data ?: emptyList()
                Log.d(TAG, "✅ mesas-estado recibidas: ${lista.size} -> ${lista.map { "id=${it.id}, estado=${it.estado}" }}")
                lista.associateBy { it.id }
            } else {
                Log.e(TAG, "❌ Error al obtener mesas-estado: ${resEstado.code()}")
                emptyMap()
            }

            // 4. OCUPACIONES ACTIVAS (para obtener el ocupacion_id que mesas-estado no devuelve)
            val resOcupaciones = RetrofitClient.instance.getOcupaciones(authHeader)
            val ocupacionesPorMesa = if (resOcupaciones.isSuccessful) {
                val lista = resOcupaciones.body()?.data ?: emptyList()
                Log.d(TAG, "✅ Ocupaciones activas: ${lista.size} -> ${lista.map { "id=${it.id}, mesa=${it.mesa_id}" }}")
                lista.associateBy { it.mesa_id }
            } else {
                Log.e(TAG, "❌ Error al obtener ocupaciones: ${resOcupaciones.code()}")
                emptyMap()
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
                    
                    // Verificar estado real desde mesas-estado o verificar si existe una ocupación activa
                    val estadoReal = estadoMesasMap[m.id]
                    // Obtener ocupacion_id desde el endpoint de ocupaciones
                    val ocupId = ocupacionesPorMesa[m.id]?.id
                    val estadoCalculado = if (estadoReal?.estado == "ocupada" || ocupId != null) EstadoMesa.OCUPADA else EstadoMesa.DISPONIBLE

                    Mesa(
                        id = m.id,
                        nombre = "Mesa ${m.numero_mesa}",
                        capacidad = m.capacidad,
                        zona = if (zonaActiva) nombreZonaLimpio else "$nombreZonaLimpio (SUSPENDIDA)",
                        zonaId = m.zona.id,
                        estado = estadoCalculado,
                        activo = if (activa) 1 else 0,
                        ocupacionId = ocupId
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
        if (currentToken.isEmpty()) {
            Log.e(TAG, "❌ TOKEN VACÍO - No se puede consultar reservaciones")
            return
        }
        val authHeader = "Bearer $currentToken"
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale("es", "MX"))
        sdf.timeZone = java.util.TimeZone.getTimeZone("America/Mexico_City")
        val fechaActual = sdf.format(java.util.Date())
        val rol = if (currentRole.equals("gerente", ignoreCase = true)) "gerente" else "staff"
        
        try {
            val response = RetrofitClient.instance.getReservaciones(authHeader, rol, fechaActual)
            if (response.isSuccessful) {
                val reservacionesApi = response.body()?.data ?: emptyList()
                val nuevasReservas = reservacionesApi.map { r ->
                    // Parse zona
                    val nombreZona = try {
                        if (r.zona != null && r.zona.isJsonObject) {
                            val obj = r.zona.asJsonObject
                            obj.get("nombre_zona")?.asString ?: obj.get("nombre")?.asString ?: "General"
                        } else if (r.zona != null && r.zona.isJsonPrimitive) {
                            r.zona.asString
                        } else {
                            "General"
                        }
                    } catch (e: Exception) { "General" }

                    // Parse zona ID
                    val zonaIdNum = try {
                        if (r.zona != null && r.zona.isJsonObject) {
                            r.zona.asJsonObject.get("id")?.asInt ?: 0
                        } else 0
                    } catch (e: Exception) { 0 }

                    // Parse promocion
                    val nombrePromocion = try {
                        if (r.promocion != null && r.promocion.isJsonObject) {
                            val obj = r.promocion.asJsonObject
                            obj.get("nombre")?.asString ?: "Sin promoción"
                        } else if (r.promocion != null && r.promocion.isJsonPrimitive) {
                            r.promocion.asString
                        } else {
                            "Sin promoción"
                        }
                    } catch (e: Exception) { "Sin promoción" }

                    // Parse precio de promocion
                    val precioPromo = try {
                        if (r.promocion != null && r.promocion.isJsonObject) {
                            val obj = r.promocion.asJsonObject
                            obj.get("precio")?.asString ?: ""
                        } else ""
                    } catch (e: Exception) { "" }

                    // Parse ocasion
                    val nombreOcasion = try {
                        if (r.ocasion != null && r.ocasion.isJsonObject) {
                            val obj = r.ocasion.asJsonObject
                            obj.get("nombre")?.asString ?: "Sin ocasión"
                        } else if (r.ocasion != null && r.ocasion.isJsonPrimitive) {
                            r.ocasion.asString
                        } else {
                            "Sin ocasión"
                        }
                    } catch (e: Exception) { "Sin ocasión" }

                    Reserva(
                        id = r.id,
                        folio = r.folio,
                        nombre = r.cafeteria?.nombre ?: "Cafetería",
                        fecha = r.fecha,
                        hora = "${r.hora_inicio.substring(0,5)} - ${r.hora_fin.substring(0,5)}",
                        personas = r.numero_personas,
                        nombreCliente = r.nombre_cliente,
                        zona = nombreZona,
                        zonaId = zonaIdNum,
                        comentarios = r.comentarios,
                        mesa = null,
                        promocion = nombrePromocion,
                        precioPromocion = precioPromo,
                        ocasion = nombreOcasion,
                        estado = r.estado
                    )
                }
                _reservas.value = ArrayList(nuevasReservas)
                Log.d(TAG, "✅ Reservaciones sincronizadas: ${nuevasReservas.size}")
            } else {
                Log.e(TAG, "❌ Error API Reservaciones: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR FATAL RED RESERVACIONES: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun checkinReservacion(idReserva: Int): Pair<Boolean, String> {
        if (currentToken.isEmpty()) {
            Log.e(TAG, "❌ TOKEN VACÍO - No se puede hacer checkin")
            return Pair(false, "Token vacío")
        }
        val authHeader = "Bearer $currentToken"
        return try {
            val response = RetrofitClient.instance.completarReservacion(authHeader, idReserva)
            if (response.isSuccessful) {
                Log.d(TAG, "✅ Checkin de reservación $idReserva exitoso (estado → en_curso)")
                fetchReservaciones()
                Pair(true, "Llegada registrada")
            } else {
                val errorStr = response.errorBody()?.string() ?: ""
                var errMsg = "Error al registrar llegada"
                try {
                    val jsonObj = org.json.JSONObject(errorStr)
                    if (jsonObj.has("message")) {
                        errMsg = jsonObj.getString("message")
                    }
                } catch(e: Exception) {}
                
                Log.e(TAG, "❌ Error al hacer checkin: ${response.code()} - $errorStr")
                Pair(false, errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR FATAL AL HACER CHECKIN: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            Pair(false, "Error de conexión")
        }
    }

    suspend fun abrirMesa(idReserva: Int, idMesa: Int, zonaId: Int, numPersonas: Int, comentarios: String?, nombreCliente: String? = null): Pair<Boolean, String> {
        if (currentToken.isEmpty()) {
            Log.e(TAG, "❌ TOKEN VACÍO - No se puede abrir mesa")
            return Pair(false, "Token vacío, inicia sesión de nuevo")
        }
        val authHeader = "Bearer $currentToken"
        return try {
            val request = com.softnamic.proyectointegradorii.core.network.AbrirMesaRequest(
                mesa_ids = listOf(idMesa),
                zona_id = zonaId,
                reservacion_id = idReserva,
                numero_personas = numPersonas,
                tipo = "reservacion",
                comentarios = comentarios,
                nombre_cliente = nombreCliente ?: "Cliente"
            )
            Log.d(TAG, "📤 Enviando abrir mesa: mesa_id=$idMesa, zona_id=$zonaId, reservacion_id=$idReserva, personas=$numPersonas, tipo=reservacion")
            val response = RetrofitClient.instance.abrirMesa(authHeader, request)
            if (response.isSuccessful) {
                // Leer el body para verificar que no es un falso HTTP 200 con success: false
                val bodyStr = response.body()?.string() ?: ""
                var isReallySuccess = true
                var errMsg = ""
                try {
                    val jsonObj = org.json.JSONObject(bodyStr)
                    if (jsonObj.has("success") && !jsonObj.getBoolean("success")) {
                        isReallySuccess = false
                        errMsg = jsonObj.optString("message", "Error del servidor")
                    }
                } catch (e: Exception) {}

                if (isReallySuccess) {
                    Log.d(TAG, "✅ Mesa abierta con éxito para la reservación $idReserva")
                    // Fetch reservations and tables again to update the local lists
                    fetchReservaciones()
                    fetchMesasYZonas()
                    Pair(true, "Mesa abierta con éxito")
                } else {
                    Log.e(TAG, "❌ Error lógico al abrir mesa: $errMsg")
                    Pair(false, errMsg)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Log.e(TAG, "❌ Error al abrir mesa: ${response.code()} - $errorBody")
                // Intentar extraer mensaje del JSON, si no es JSON (ej. HTML de error 500) mostrar mensaje limpio
                var errMsg = "Error del servidor (${response.code()})"
                try {
                    val jsonObj = org.json.JSONObject(errorBody)
                    if (jsonObj.has("message")) {
                        errMsg = jsonObj.getString("message")
                    }
                } catch (e: Exception) {
                    // No es JSON (probablemente HTML), dejar mensaje genérico
                    if (response.code() == 500) errMsg = "Error interno del servidor. Revisa los logs de Laravel (storage/logs/laravel.log)"
                }
                Pair(false, errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR FATAL AL ABRIR MESA: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            Pair(false, "Error de red: ${e.message}")
        }
    }

    suspend fun finalizarOcupacion(idOcupacion: Int): Pair<Boolean, String> {
        if (currentToken.isEmpty()) return Pair(false, "Token vacío")
        return try {
            val response = RetrofitClient.instance.finalizarOcupacion("Bearer $currentToken", idOcupacion)
            if (response.isSuccessful) {
                fetchMesasYZonas()
                fetchReservaciones()
                Pair(true, "Mesa liberada con éxito")
            } else Pair(false, "Error al liberar mesa")
        } catch (e: Exception) {
            Pair(false, "Error de conexión")
        }
    }

    suspend fun cancelarReservacion(idReservacion: Int): Pair<Boolean, String> {
        if (currentToken.isEmpty()) return Pair(false, "Token vacío")
        val rol = if (currentRole.equals("gerente", ignoreCase = true)) "gerente" else "staff"
        return try {
            val response = RetrofitClient.instance.cancelarReservacion("Bearer $currentToken", rol, idReservacion)
            if (response.isSuccessful) {
                fetchReservaciones()
                Pair(true, "Reservación cancelada")
            } else Pair(false, "Error al cancelar")
        } catch (e: Exception) {
            Pair(false, "Error de conexión")
        }
    }
}
