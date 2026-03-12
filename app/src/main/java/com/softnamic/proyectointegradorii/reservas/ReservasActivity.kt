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

        dialogView.findViewById<TextView>(R.id.tvDetalleNombre).text = reserva.nombre
        dialogView.findViewById<TextView>(R.id.tvDetalleHora).text = reserva.hora
        dialogView.findViewById<TextView>(R.id.tvDetallePersonas).text = "Personas: ${reserva.personas}"
        dialogView.findViewById<TextView>(R.id.tvDetalleZona).text = "Zona: ${reserva.zona}"
        dialogView.findViewById<TextView>(R.id.tvDetalleMesa).text = reserva.mesa ?: "Sin mesa asignada"
        dialogView.findViewById<TextView>(R.id.tvDetalleComentarios).text = reserva.comentarios ?: "Sin comentarios"
        dialogView.findViewById<TextView>(R.id.tvDetallePromocion).text = "Promoción aplicada:\n${reserva.promocion}"

        val spinner = dialogView.findViewById<Spinner>(R.id.spAccion)
        val acciones = listOf("Asignar mesa", "Cancelar reservación", "Llegó", "No llegó")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, acciones)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.btnAplicar).setOnClickListener {
            // NOTE: Ideally, these actions would be passed to the ViewModel and then the API
            when (spinner.selectedItem.toString()) {
                "Asignar mesa" -> {
                    Toast.makeText(this, "Asignar mesa", Toast.LENGTH_SHORT).show()
                }
                "Cancelar reservación" -> {
                    Toast.makeText(this, "Cancelar reservación", Toast.LENGTH_SHORT).show()
                }
                "Llegó" -> {
                    // reserva.estado = "Llegó" // Can't update directly here if strictly respecting single source of truth, but UI will rely on next fetch.
                    Toast.makeText(this, "Marcado como Llegó", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                "No llegó" -> {
                    Toast.makeText(this, "Marcado como No llegó", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else -> {
                    Toast.makeText(this, "Selecciona una acción", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
        }

        dialog.show()
    }
}
