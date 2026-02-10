package com.softnamic.proyectointegradorii.reservas

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.BaseActivity
import com.softnamic.proyectointegradorii.R

class ReservasActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        // Indicamos al menú inferior que estamos en "Reservas"
        configurarMenuInferior(R.id.bottom_reservations)

        val recycler = findViewById<RecyclerView>(R.id.rvReservas)

        val listaReservas = listOf(
            Reserva(
                nombre = "María López",
                hora = "12:30 pm",
                personas = 4,
                zona = "Fumadores",
                comentarios = "Mesa cerca de la ventana",
                mesa = "Mesa 3"
            ),
            Reserva(
                nombre = "Juan Pérez",
                hora = "1:00 pm",
                personas = 2,
                zona = "No fumadores",
                comentarios = "Cumpleaños",
                mesa = null
            ),
            Reserva(
                nombre = "Ana Torres",
                hora = "2:15 pm",
                personas = 5,
                zona = "Terraza",
                comentarios = "Silla para bebé",
                mesa = "Mesa 5"
            )
        )

        val adapter = ReservaAdapter(listaReservas) { reserva ->
            mostrarDetalleReserva(reserva)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    // -------- DETALLE DE RESERVA --------
    private fun mostrarDetalleReserva(reserva: Reserva) {

        val dialogView = layoutInflater.inflate(
            R.layout.dialog_detalle_reserva,
            null
        )

        // TextViews del diálogo
        dialogView.findViewById<TextView>(R.id.tvDetalleNombre).text = reserva.nombre
        dialogView.findViewById<TextView>(R.id.tvDetalleHora).text = reserva.hora
        dialogView.findViewById<TextView>(R.id.tvDetallePersonas).text =
            "Personas: ${reserva.personas}"
        dialogView.findViewById<TextView>(R.id.tvDetalleZona).text =
            "Zona: ${reserva.zona}"
        dialogView.findViewById<TextView>(R.id.tvDetalleMesa).text =
            reserva.mesa ?: "Sin mesa asignada"
        dialogView.findViewById<TextView>(R.id.tvDetalleComentarios).text =
            reserva.comentarios ?: "Sin comentarios"

        // Spinner de acciones
        val spinner = dialogView.findViewById<Spinner>(R.id.spAccion)

        val acciones = listOf(
            "Asignar mesa",
            "Cancelar reservación"
        )

        spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            acciones
        )

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Botón aplicar
        dialogView.findViewById<Button>(R.id.btnAplicar).setOnClickListener {

            when (spinner.selectedItem.toString()) {
                "Asignar mesa" -> {
                    Toast.makeText(this, "Asignar mesa", Toast.LENGTH_SHORT).show()
                }
                "Cancelar reservación" -> {
                    Toast.makeText(this, "Cancelar reservación", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Selecciona una acción", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }
}
