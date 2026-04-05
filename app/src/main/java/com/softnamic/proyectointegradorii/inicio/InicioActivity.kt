package com.softnamic.proyectointegradorii.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import com.softnamic.proyectointegradorii.mesas.EstadoMesa
import com.softnamic.proyectointegradorii.mesas.MesasActivity
import com.softnamic.proyectointegradorii.reservas.ReservasActivity
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
        val tvReservasHoyLabel = findViewById<TextView>(R.id.tvReservasHoyLabel)
        val tvProximaReserva = findViewById<TextView>(R.id.tvProximaReserva)

        val tvMesasTotales = findViewById<TextView>(R.id.tvMesasTotales)
        val tvMesasDisponibles = findViewById<TextView>(R.id.tvMesasDisponibles)
        val tvMesasOcupadas = findViewById<TextView>(R.id.tvMesasOcupadas)

        val llProximasReservas = findViewById<android.widget.LinearLayout>(R.id.llProximasReservas)
        val tvNoReservas = findViewById<TextView>(R.id.tvNoReservas)

        lifecycleScope.launch {
            launch {
                RestaurantRepository.reservas.collect { reservas ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("es", "MX"))
                    sdf.timeZone = TimeZone.getTimeZone("America/Mexico_City")
                    val hoy = sdf.format(Date())
                    
                    val sdfTime = SimpleDateFormat("HH:mm", Locale("es", "MX"))
                    sdfTime.timeZone = TimeZone.getTimeZone("America/Mexico_City")
                    val ahora = sdfTime.format(Date())
                    
                    val activasHoy = reservas.filter { r ->
                        r.fecha == hoy &&
                        r.estado?.lowercase() != "finalizada" &&
                        r.estado?.lowercase() != "cancelada" &&
                        r.estado?.lowercase() != "no_show"
                    }
                    
                    tvReservasHoyLabel.text = "Reservas: ${activasHoy.size}"
                    
                    val proximas = activasHoy.filter { r ->
                        val horaReserva = r.hora.take(5)
                        horaReserva >= ahora
                    }.sortedBy { it.hora.take(5) }
                    
                    if (proximas.isNotEmpty()) {
                        val primeraHora = proximas.first().hora.take(5)
                        var hr = primeraHora.substring(0, 2).toIntOrNull() ?: 12
                        val min = primeraHora.substring(3, 5)
                        val ampm = if (hr >= 12) "pm" else "am"
                        if (hr > 12) hr -= 12
                        if (hr == 0) hr = 12
                        tvProximaReserva.text = "Próxima: $hr:$min $ampm"
                    } else {
                        tvProximaReserva.text = "Próxima: --"
                    }

                    llProximasReservas.removeAllViews()
                    if (proximas.isEmpty()) {
                        tvNoReservas.text = "No hay más reservas próximas para el día de hoy"
                        llProximasReservas.addView(tvNoReservas)
                    } else {
                        val top3 = proximas.take(3)
                        top3.forEach { res ->
                            val hrNum = res.hora.take(2).toIntOrNull() ?: 12
                            val min = res.hora.substring(3, 5)
                            val ampm = if (hrNum >= 12) "pm" else "am"
                            var h = hrNum
                            if (h > 12) h -= 12
                            if (h == 0) h = 12
                            val horaStr = "$h:$min $ampm"
                            
                            val row = android.widget.LinearLayout(this@InicioActivity)
                            row.orientation = android.widget.LinearLayout.HORIZONTAL
                            row.setPadding(0, 24, 0, 24)
                            
                            val appFont = androidx.core.content.res.ResourcesCompat.getFont(this@InicioActivity, R.font.lato)
                            
                            val tvHora = TextView(this@InicioActivity)
                            tvHora.text = horaStr
                            tvHora.textSize = 16f
                            tvHora.typeface = appFont
                            tvHora.setTextColor(androidx.core.content.ContextCompat.getColor(this@InicioActivity, R.color.text_color_primary))
                            tvHora.layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f)
                            
                            val tvNombre = TextView(this@InicioActivity)
                            tvNombre.text = res.nombreCliente ?: "Cliente"
                            tvNombre.textSize = 16f
                            tvNombre.typeface = appFont
                            tvNombre.setTextColor(androidx.core.content.ContextCompat.getColor(this@InicioActivity, R.color.text_color_primary))
                            tvNombre.layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f)
                            tvNombre.ellipsize = android.text.TextUtils.TruncateAt.END
                            tvNombre.maxLines = 1
                            
                            val tvPax = TextView(this@InicioActivity)
                            tvPax.text = "${res.personas} personas"
                            tvPax.textSize = 16f
                            tvPax.typeface = appFont
                            tvPax.setTextColor(androidx.core.content.ContextCompat.getColor(this@InicioActivity, R.color.text_color_secondary))
                            tvPax.layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.3f)
                            tvPax.gravity = android.view.Gravity.END
                            
                            row.addView(tvHora)
                            row.addView(tvNombre)
                            row.addView(tvPax)
                            
                            llProximasReservas.addView(row)
                            
                            val sep = android.view.View(this@InicioActivity)
                            val sepParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 2)
                            sepParams.setMargins(0, 0, 0, 0)
                            sep.layoutParams = sepParams
                            sep.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this@InicioActivity, R.color.border_light))
                            llProximasReservas.addView(sep)
                        }
                    }
                }
            }
            launch {
                RestaurantRepository.mesas.collect { mesas ->
                    val activos = mesas.filter { it.activo == 1 }
                    val totalMesas = activos.size
                    val disponibles = activos.count { it.estado == EstadoMesa.DISPONIBLE }
                    val ocupadas = activos.count { it.estado == EstadoMesa.OCUPADA }
                    
                    tvMesasTotales.text = "Mesas totales: $totalMesas"
                    tvMesasDisponibles.text = "Disponibles: $disponibles"
                    tvMesasOcupadas.text = "Ocupadas: $ocupadas"
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
        val cardReservas = findViewById<MaterialCardView>(R.id.cardReservas)
        val cardMesas = findViewById<MaterialCardView>(R.id.cardMesas)
        
        // 1. Nombre de la cafetería
        tvNombreCafeteria.text = cafeName.uppercase()
        
        // 2. Fecha actual formateada (Forzando zona horaria de México para evitar un día adelantado)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        sdf.timeZone = TimeZone.getTimeZone("America/Mexico_City")
        tvFecha.text = sdf.format(Date())

        // 3. Saludo al usuario
        tvBienvenida.text = "¡Bienvenido, $name!"

        // Click listeners para los cards
        cardReservas.setOnClickListener {
            val intent = Intent(this, ReservasActivity::class.java)
            startActivity(intent)
        }

        cardMesas.setOnClickListener {
            val intent = Intent(this, MesasActivity::class.java)
            startActivity(intent)
        }
    }
}
