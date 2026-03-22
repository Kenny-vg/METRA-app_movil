package com.softnamic.proyectointegradorii.reservas

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReservasActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ReservaAdapter

    private val viewModel: ReservasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_reservations)

        recycler = findViewById(R.id.rvReservas)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ReservaAdapter { reserva ->
            mostrarDetalleReserva(reserva)
        }
        recycler.adapter = adapter

        observarViewModel()
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.reservas.collectLatest { reservas ->
                    adapter.submitList(reservas)
                }
            }
        }
    }

    private fun mostrarDetalleReserva(reserva: Reserva) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalle_reserva, null)

        dialogView.findViewById<TextView>(R.id.tvDetalleFolio).text = "Folio: ${reserva.folio}"
        dialogView.findViewById<TextView>(R.id.tvDetalleCliente).text = "Cliente: ${reserva.nombreCliente ?: "Desconocido"}"
        dialogView.findViewById<TextView>(R.id.tvDetalleNombre).text = "Cafetería: ${reserva.nombre}"
        dialogView.findViewById<TextView>(R.id.tvDetalleFecha).text = "Fecha: ${reserva.fecha}"
        dialogView.findViewById<TextView>(R.id.tvDetalleHora).text = "Hora: ${reserva.hora}"
        dialogView.findViewById<TextView>(R.id.tvDetallePersonas).text = "Personas: ${reserva.personas}"
        dialogView.findViewById<TextView>(R.id.tvDetalleEstado).text = "Estado: ${reserva.estado?.capitalize() ?: "Pendiente"}"
        dialogView.findViewById<TextView>(R.id.tvDetalleOcasion).text = "Ocasión: ${reserva.ocasion}"
        dialogView.findViewById<TextView>(R.id.tvDetalleZona).text = "Zona: ${reserva.zona}"
        dialogView.findViewById<TextView>(R.id.tvDetalleMesa).text = "Mesa: ${reserva.mesa ?: "Sin asignar"}"
        dialogView.findViewById<TextView>(R.id.tvDetalleComentarios).text = "Comentarios: ${reserva.comentarios ?: "Sin comentarios"}"
        dialogView.findViewById<TextView>(R.id.tvDetallePromocion).text = "Promoción aplicada:\n${reserva.promocion}"

        // Mostrar precio de promoción
        val tvPrecio = dialogView.findViewById<TextView>(R.id.tvDetallePrecioPromocion)
        if (reserva.precioPromocion.isNotEmpty()) {
            tvPrecio.text = "Precio: \$${reserva.precioPromocion}"
            tvPrecio.visibility = android.view.View.VISIBLE
        } else {
            tvPrecio.visibility = android.view.View.GONE
        }

        // Ocultar ocasión si no aplica
        val tvOcasion = dialogView.findViewById<TextView>(R.id.tvDetalleOcasion)
        if (reserva.ocasion == "Sin ocasión") {
            tvOcasion.visibility = android.view.View.GONE
        }

        // ─── Sección: Mesas recomendadas (siempre visible) ───
        val llMesasRecomendadas = dialogView.findViewById<android.widget.LinearLayout>(R.id.llMesasRecomendadas)


        val mesasEnZona = viewModel.mesas.value.filter {
            it.zona.equals(reserva.zona, ignoreCase = true) &&
            it.estado == com.softnamic.proyectointegradorii.mesas.EstadoMesa.DISPONIBLE &&
            it.activo == 1
        }

        if (mesasEnZona.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "No hay mesas disponibles en la zona \"${reserva.zona}\""
                setTextColor(android.graphics.Color.parseColor("#999999"))
                textSize = 13f
                setPadding(0, 4, 0, 4)
            }
            llMesasRecomendadas.addView(tvVacio)
        } else {
            mesasEnZona.forEach { mesa ->
                val tvMesa = TextView(this).apply {
                    text = "• ${mesa.nombre}  —  Capacidad: ${mesa.capacidad} personas"
                    textSize = 14f
                    setPadding(0, 6, 0, 6)
                }
                llMesasRecomendadas.addView(tvMesa)
            }
        }

        // ─── Spinner de acciones ───
        val spinner = dialogView.findViewById<Spinner>(R.id.spAccion)
        val spMesas = dialogView.findViewById<Spinner>(R.id.spMesasDisponibles)
        val tvMesasDisponiblesLabel = dialogView.findViewById<TextView>(R.id.tvMesasDisponiblesLabel)

        val acciones = mutableListOf<String>()
        val estadoActual = reserva.estado?.lowercase() ?: "pendiente"

        if (estadoActual == "pendiente") {
            acciones.add("Llegó y asignar mesa")
            acciones.add("Sólo marcar llegada")
            acciones.add("Cancelar reservación")
        } else if (estadoActual == "en_curso") {
            // El cliente ya hizo checkin pero puede que no tenga mesa asignada aún
            acciones.add("Asignar mesa")
        }
        // Si es "finalizada" o "cancelada" no hay acciones disponibles

        if (acciones.isEmpty()) {
            spinner.visibility = android.view.View.GONE
            spMesas.visibility = android.view.View.GONE
            tvMesasDisponiblesLabel.visibility = android.view.View.GONE
            dialogView.findViewById<Button>(R.id.btnAplicar).visibility = android.view.View.GONE
        } else {
            val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item_custom, acciones)
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
            spinner.adapter = spinnerAdapter

            // Spinner de mesas: si la reserva no tiene zona, mostrar TODAS las disponibles
            val tieneZona = !reserva.zona.isNullOrBlank() && reserva.zona != "General"
            val mesasDisponibles = viewModel.mesas.value.filter {
                it.estado == com.softnamic.proyectointegradorii.mesas.EstadoMesa.DISPONIBLE && it.activo == 1 &&
                (!tieneZona || it.zona.equals(reserva.zona, ignoreCase = true))
            }
            val nombresMesas = mesasDisponibles.map { "${it.nombre} (${it.zona})" }
            val mesasAdapter = ArrayAdapter(
                this,
                R.layout.spinner_item_custom,
                if (nombresMesas.isEmpty()) listOf(if (tieneZona) "No hay mesas en ${reserva.zona}" else "No hay mesas disponibles") else nombresMesas
            )
            mesasAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
            spMesas.adapter = mesasAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    val selected = acciones[position]
                    val isMesaRequerida = selected == "Llegó y asignar mesa" || selected == "Asignar mesa"
                    spMesas.visibility = if (isMesaRequerida) android.view.View.VISIBLE else android.view.View.GONE
                    tvMesasDisponiblesLabel.visibility = if (isMesaRequerida) android.view.View.VISIBLE else android.view.View.GONE
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.btnAplicar).setOnClickListener {
            val accionSeleccionada = spinner.selectedItem?.toString() ?: return@setOnClickListener

            // Helper para obtener la mesa seleccionada
            val tieneZona = !reserva.zona.isNullOrBlank() && reserva.zona != "General"
            val mesasDisponibles = viewModel.mesas.value.filter {
                it.estado == com.softnamic.proyectointegradorii.mesas.EstadoMesa.DISPONIBLE && it.activo == 1 &&
                (!tieneZona || it.zona.equals(reserva.zona, ignoreCase = true))
            }

            if (accionSeleccionada == "Sólo marcar llegada") {
                dialog.dismiss()
                Toast.makeText(this, "Registrando llegada de cliente...", Toast.LENGTH_SHORT).show()
                viewModel.checkinReservacion(reserva.id) { exito, msg ->
                    runOnUiThread {
                        if (exito) Toast.makeText(this, "Llegada registrada (en curso)", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                }

            } else if (accionSeleccionada == "Llegó y asignar mesa") {
                if (mesasDisponibles.isEmpty()) {
                    Toast.makeText(this, "No hay mesas disponibles en la zona ${reserva.zona}", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val selectedMesaIdx = spMesas.selectedItemPosition
                if (selectedMesaIdx < 0 || selectedMesaIdx >= mesasDisponibles.size) return@setOnClickListener
                val mesaSeleccionada = mesasDisponibles[selectedMesaIdx]
                val mesaId = mesaSeleccionada.id
                val zonaIdMesa = mesaSeleccionada.zonaId

                dialog.dismiss()
                Toast.makeText(this, "Abriendo mesa y registrando llegada...", Toast.LENGTH_SHORT).show()
                
                // POST /ocupaciones hace TODO: crea la ocupación Y cambia la reserva a en_curso
                viewModel.abrirMesa(reserva.id, mesaId, zonaIdMesa, reserva.personas, reserva.comentarios) { exito, msg ->
                    runOnUiThread {
                        if (exito) Toast.makeText(this, "✅ Cliente sentado y mesa abierta", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                    }
                }

            } else if (accionSeleccionada == "Asignar mesa") {
                if (mesasDisponibles.isEmpty()) {
                    Toast.makeText(this, "No hay mesas disponibles libres en ${reserva.zona}", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val selectedMesaIdx = spMesas.selectedItemPosition
                if (selectedMesaIdx < 0 || selectedMesaIdx >= mesasDisponibles.size) return@setOnClickListener
                val mesaSeleccionada = mesasDisponibles[selectedMesaIdx]
                val mesaId = mesaSeleccionada.id
                val zonaIdMesa = mesaSeleccionada.zonaId

                dialog.dismiss()
                Toast.makeText(this, "Asignando mesa...", Toast.LENGTH_SHORT).show()
                viewModel.abrirMesa(reserva.id, mesaId, zonaIdMesa, reserva.personas, reserva.comentarios) { exito, msg ->
                    runOnUiThread {
                        if (exito) Toast.makeText(this, "✅ Mesa asignada con éxito", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                    }
                }

            } else if (accionSeleccionada == "Cancelar reservación") {
                dialog.dismiss()
                Toast.makeText(this, "Cancelando reservación...", Toast.LENGTH_SHORT).show()
                viewModel.cancelarReservacion(reserva.id) { exito, msg ->
                    runOnUiThread {
                        if (exito) Toast.makeText(this, "Reservación cancelada", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        dialog.show()
    }
}
