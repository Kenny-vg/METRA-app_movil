package com.softnamic.proyectointegradorii.inicio

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import com.softnamic.proyectointegradorii.mesas.EstadoMesa
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InicioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        configurarVista()
        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_home)
        observarDatos()
    }

    private fun observarDatos() {
        val tvReservasCount = findViewById<TextView>(R.id.tvReservasCount)
        val tvMesasCount = findViewById<TextView>(R.id.tvMesasCount)

        lifecycleScope.launch {
            launch {
                RestaurantRepository.reservas.collect { reservas ->
                    tvReservasCount.text = reservas.size.toString()
                }
            }
            launch {
                RestaurantRepository.mesas.collect { mesas ->
                    val disponibles = mesas.count { it.estado == EstadoMesa.DISPONIBLE && it.activo == 1 }
                    tvMesasCount.text = disponibles.toString()
                }
            }
        }
    }

    private fun configurarVista() {
        val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
        
        val name = prefs.getString("NAME", "") ?: "Usuario"
        val cafeName = prefs.getString("CAFE_NAME", "CAFETERÍA") ?: "CAFETERÍA"

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val tvNombreCafeteria = findViewById<TextView>(R.id.tvNombreCafeteria)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        
        // 1. Nombre de la cafetería
        tvNombreCafeteria.text = cafeName.uppercase()
        
        // 2. Fecha actual formateada (Forzando zona horaria de México para evitar un día adelantado)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        sdf.timeZone = TimeZone.getTimeZone("America/Mexico_City")
        tvFecha.text = sdf.format(Date())

        // 3. Saludo al usuario
        tvBienvenida.text = "¡Bienvenido, $name!"
    }
}
