package com.softnamic.proyectointegradorii.reservas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.core.pruebas.DataMock
import com.softnamic.proyectointegradorii.R

class ReservasActivity : BaseActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ReservaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        configurarMenuInferior(R.id.bottom_reservations)

        recycler = findViewById(R.id.rvReservas)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ReservaAdapter(DataMock.reservas) { reserva ->
            mostrarDetalleReserva(reserva)
        }
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Notifica al adaptador que los datos pueden haber cambiado
        adapter.notifyDataSetChanged()
    }

    private fun mostrarDetalleReserva(reserva: Reserva) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalle_reserva, null)

        dialogView.findViewById<TextView>(R.id.tvDetalleNombre).text = reserva.nombre
        dialogView.findViewById<TextView>(R.id.tvDetalleHora).text = reserva.hora
        dialogView.findViewById<TextView>(R.id.tvDetallePersonas).text = "Personas: ${reserva.personas}"
        dialogView.findViewById<TextView>(R.id.tvDetalleZona).text = "Zona: ${reserva.zona}"
        dialogView.findViewById<TextView>(R.id.tvDetalleMesa).text = reserva.mesa ?: "Sin mesa asignada"
        dialogView.findViewById<TextView>(R.id.tvDetalleComentarios).text = reserva.comentarios ?: "Sin comentarios"

        val spinner = dialogView.findViewById<Spinner>(R.id.spAccion)
        val acciones = listOf("Asignar mesa", "Cancelar reservación", "Llegó", "No llegó")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, acciones)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<Button>(R.id.btnAplicar).setOnClickListener {
            when (spinner.selectedItem.toString()) {
                "Asignar mesa" -> {
                    Toast.makeText(this, "Asignar mesa", Toast.LENGTH_SHORT).show()
                }
                "Cancelar reservación" -> {
                    Toast.makeText(this, "Cancelar reservación", Toast.LENGTH_SHORT).show()
                }
                "Llegó" -> {
                    reserva.estado = "Llegó"
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                "No llegó" -> {
                    reserva.estado = "No llegó"
                    adapter.notifyDataSetChanged()
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

