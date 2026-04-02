package com.softnamic.proyectointegradorii.reservas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
    private lateinit var etBuscar: EditText

    private val viewModel: ReservasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_reservations)

        recycler = findViewById(R.id.rvReservas)
        etBuscar = findViewById(R.id.etBuscar)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ReservaAdapter { reserva ->
            mostrarDetalleReserva(reserva)
        }
        recycler.adapter = adapter

        val btnHoy = findViewById<Button>(R.id.btnHoy)
        val btnProximas = findViewById<Button>(R.id.btnProximas)
        
        btnHoy.setOnClickListener { viewModel.seleccionarTab("Hoy") }
        btnProximas.setOnClickListener { viewModel.seleccionarTab("Próximas") }

        configurarBuscador()
        observarViewModel(btnHoy, btnProximas)
    }

    private fun configurarBuscador() {
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.buscar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observarViewModel(btnHoy: Button, btnProximas: Button) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.reservasFiltradas.collectLatest { reservas ->
                        adapter.submitList(reservas)
                    }
                }
                launch {
                    viewModel.filtroTabs.collectLatest { tab ->
                        if (tab == "Hoy") {
                            btnHoy.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this@ReservasActivity, R.color.coffee_primary))
                            btnHoy.setTextColor(android.graphics.Color.WHITE)
                            btnProximas.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            btnProximas.setTextColor(androidx.core.content.ContextCompat.getColor(this@ReservasActivity, R.color.coffee_primary))
                        } else {
                            btnProximas.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this@ReservasActivity, R.color.coffee_primary))
                            btnProximas.setTextColor(android.graphics.Color.WHITE)
                            btnHoy.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            btnHoy.setTextColor(androidx.core.content.ContextCompat.getColor(this@ReservasActivity, R.color.coffee_primary))
                        }
                    }
                }
            }
        }
    }

    private fun mostrarDetalleReserva(reserva: Reserva) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalle_reserva, null)

        dialogView.findViewById<TextView>(R.id.tvDetalleFolio).text = reserva.folio
        dialogView.findViewById<TextView>(R.id.tvDetalleCliente).text = reserva.nombreCliente ?: "Cliente desconocido"
        dialogView.findViewById<TextView>(R.id.tvDetalleNombre).text = reserva.nombre
        dialogView.findViewById<TextView>(R.id.tvDetalleFecha).text = reserva.fecha
        dialogView.findViewById<TextView>(R.id.tvDetalleHora).text = reserva.hora
        dialogView.findViewById<TextView>(R.id.tvDetallePersonas).text = "${reserva.personas} personas"
        dialogView.findViewById<TextView>(R.id.tvDetalleEstado).text = (reserva.estado ?: "Pendiente").uppercase()
        dialogView.findViewById<TextView>(R.id.tvDetalleOcasion).text = "OCASIÓN: ${reserva.ocasion}"
        dialogView.findViewById<TextView>(R.id.tvDetalleZona).text = "ZONA: ${reserva.zona}"
        dialogView.findViewById<TextView>(R.id.tvDetalleMesa).text = "MESA: ${reserva.mesa ?: "Sin asignar"}"
        dialogView.findViewById<TextView>(R.id.tvDetalleComentarios).text = "NOTAS: ${reserva.comentarios ?: "Sin comentarios"}"
        dialogView.findViewById<TextView>(R.id.tvDetallePromocion).text = "PROMOCIÓN: ${reserva.promocion}"

        val tvPrecio = dialogView.findViewById<TextView>(R.id.tvDetallePrecioPromocion)
        if (reserva.precioPromocion.isNotEmpty()) {
            tvPrecio.text = "Precio: \$${reserva.precioPromocion}"
            tvPrecio.visibility = View.VISIBLE
        } else {
            tvPrecio.visibility = View.GONE
        }

        val tvOcasion = dialogView.findViewById<TextView>(R.id.tvDetalleOcasion)
        if (reserva.ocasion == "Sin ocasión") {
            tvOcasion.visibility = View.GONE
        }



        val spinner = dialogView.findViewById<Spinner>(R.id.spAccion)
        val llMesasCheckboxes = dialogView.findViewById<LinearLayout>(R.id.llMesasCheckboxes)
        val tvMesasDisponiblesLabel = dialogView.findViewById<TextView>(R.id.tvMesasDisponiblesLabel)

        val acciones = mutableListOf<String>()
        val estadoActual = reserva.estado?.lowercase() ?: "pendiente"

        if (estadoActual == "pendiente") {
            acciones.add("Llegó y asignar mesa")
            acciones.add("Cancelar reservación")
        } else if (estadoActual == "en_curso") {
            acciones.add("Asignar mesa")
        }

        val checkBoxes = mutableListOf<CheckBox>()

        if (acciones.isEmpty()) {
            spinner.visibility = View.GONE
            llMesasCheckboxes.visibility = View.GONE
            tvMesasDisponiblesLabel.visibility = View.GONE
            dialogView.findViewById<Button>(R.id.btnAplicar).visibility = View.GONE
        } else {
            val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item_custom, acciones)
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
            spinner.adapter = spinnerAdapter

            val tieneZona = !reserva.zona.isNullOrBlank() && reserva.zona != "General"
            val mesasDisponibles = viewModel.mesas.value.filter {
                it.estado == com.softnamic.proyectointegradorii.mesas.EstadoMesa.DISPONIBLE && it.activo == 1 &&
                (!tieneZona || it.zona.equals(reserva.zona, ignoreCase = true))
            }
            
            llMesasCheckboxes.removeAllViews()

            if (mesasDisponibles.isEmpty()) {
                val tvVacia = TextView(this).apply {
                    text = if (tieneZona) "No hay mesas en ${reserva.zona}" else "No hay mesas disponibles"
                    setTextColor(android.graphics.Color.parseColor("#9E9E9E"))
                }
                llMesasCheckboxes.addView(tvVacia)
            } else {
                val updateCheckboxes = {
                    val seleccionadas = checkBoxes.filter { it.isChecked }.map { it.tag as com.softnamic.proyectointegradorii.mesas.Mesa }
                    val capacidadTotal = seleccionadas.sumOf { it.capacidad }
                    if (capacidadTotal >= reserva.personas && seleccionadas.isNotEmpty()) {
                        checkBoxes.filter { !it.isChecked }.forEach { it.isEnabled = false }
                    } else {
                        checkBoxes.forEach { it.isEnabled = true }
                    }
                }

                mesasDisponibles.forEach { mesa ->
                    val cb = CheckBox(this).apply {
                        text = "${mesa.nombre} (Cap: ${mesa.capacidad})"
                        tag = mesa
                        setOnCheckedChangeListener { _, _ -> updateCheckboxes() }
                    }
                    llMesasCheckboxes.addView(cb)
                    checkBoxes.add(cb)
                }
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = acciones[position]
                    val isMesaRequerida = selected == "Llegó y asignar mesa" || selected == "Asignar mesa"
                    llMesasCheckboxes.visibility = if (isMesaRequerida) View.VISIBLE else View.GONE
                    tvMesasDisponiblesLabel.visibility = if (isMesaRequerida) View.VISIBLE else View.GONE
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnAplicar).setOnClickListener {
            val accionSeleccionada = spinner.selectedItem?.toString() ?: return@setOnClickListener
            val tieneZona = !reserva.zona.isNullOrBlank() && reserva.zona != "General"
            val mesasDisponibles = viewModel.mesas.value.filter {
                it.estado == com.softnamic.proyectointegradorii.mesas.EstadoMesa.DISPONIBLE && it.activo == 1 &&
                (!tieneZona || it.zona.equals(reserva.zona, ignoreCase = true))
            }

            if (accionSeleccionada == "Llegó y asignar mesa" || accionSeleccionada == "Asignar mesa") {
                val seleccionadas = checkBoxes.filter { it.isChecked }.map { it.tag as com.softnamic.proyectointegradorii.mesas.Mesa }
                if (seleccionadas.isEmpty()) {
                    Toast.makeText(this, "Seleccione al menos una mesa al asignar", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                
                val mesaIds = seleccionadas.map { it.id }
                val zonaId = seleccionadas.first().zonaId 

                dialog.dismiss()

                Toast.makeText(this, "Asignando mesa(s)...", Toast.LENGTH_SHORT).show()
                viewModel.abrirMesa(reserva.id, mesaIds, zonaId, reserva.personas, reserva.comentarios, reserva.nombreCliente) { exito, msg ->
                    runOnUiThread {
                        if (exito) Toast.makeText(this, "Operación exitosa", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (accionSeleccionada == "Cancelar reservación") {
                dialog.dismiss()
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
