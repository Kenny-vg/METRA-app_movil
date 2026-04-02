package com.softnamic.proyectointegradorii.wailkin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import com.softnamic.proyectointegradorii.mesas.EstadoMesa
import com.softnamic.proyectointegradorii.mesas.Mesa
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegistrarClienteActivity : BaseActivity() {

    private val checkBoxes = mutableListOf<CheckBox>()
    private var mesasActuales = listOf<Mesa>()
    private var llMesasCheckboxes: LinearLayout? = null
    private var spNumeroPersonas: AutoCompleteTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)

        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_profile)

        spNumeroPersonas = findViewById(R.id.spNumeroPersonas)
        val spZona = findViewById<AutoCompleteTextView>(R.id.spZona)
        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar)
        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etComentarios = findViewById<TextInputEditText>(R.id.etComentarios)
        llMesasCheckboxes = findViewById(R.id.llMesasCheckboxes)

        val adapterPersonas = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            (1..20).map { it.toString() })
        spNumeroPersonas?.setAdapter(adapterPersonas)

        lifecycleScope.launch {
            launch {
                RestaurantRepository.zonas.collectLatest { zonas ->
                    val zonasReales = zonas.filter { it != "Todas" }
                    val adapterZonas = ArrayAdapter(
                        this@RegistrarClienteActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        zonasReales
                    )
                    spZona.setAdapter(adapterZonas)
                }
            }

            launch {
                RestaurantRepository.mesas.collectLatest { mesas ->
                    mesasActuales = mesas
                    actualizarCheckboxes(spZona.text.toString())
                }
            }
        }

        spZona.setOnItemClickListener { _, _, position, _ ->
            val selectedZona = spZona.adapter.getItem(position).toString()
            actualizarCheckboxes(selectedZona)
        }

        spNumeroPersonas?.setOnItemClickListener { _, _, _, _ ->
            // Re-evaluar checkboxes cuando cambia el número de personas
            actualizarFiltrosCapacidad()
        }

        btnConfirmar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val personasStr = spNumeroPersonas?.text.toString()
            val zona = spZona.text.toString()
            val comentarios = etComentarios.text.toString()

            if (nombre.isEmpty()) {
                etNombre.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            val personas = personasStr.toIntOrNull()
            if (personas == null || personas <= 0) {
                spNumeroPersonas?.error = "Selecciona un número de personas"
                return@setOnClickListener
            }

            if (zona.isEmpty()) {
                spZona.error = "Debe seleccionar una zona"
                return@setOnClickListener
            }

            val seleccionadas = checkBoxes.filter { it.isChecked }.map { it.tag as Mesa }
            if (seleccionadas.isEmpty()) {
                Toast.makeText(this, "Seleccione al menos una mesa al asignar", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val mesaIds = seleccionadas.map { it.id }
            val zonaId = seleccionadas.first().zonaId 
            
            btnConfirmar.isEnabled = false
            Toast.makeText(this, "Asignando mesas...", Toast.LENGTH_SHORT).show()
            
            lifecycleScope.launch {
                val (exito, msg) = RestaurantRepository.abrirMesa(
                    idReserva = null, 
                    mesaIds = mesaIds, 
                    zonaId = zonaId, 
                    numPersonas = personas, 
                    comentarios = comentarios, 
                    nombreCliente = nombre
                )
                
                btnConfirmar.isEnabled = true
                if (exito) {
                    Toast.makeText(this@RegistrarClienteActivity, "Registro y asignación exitosos", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegistrarClienteActivity, "Error: $msg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun actualizarCheckboxes(zonaSeleccionada: String) {
        val mesasDisponiblesEnZona = mesasActuales.filter {
            it.zona.equals(zonaSeleccionada, ignoreCase = true) &&
            it.estado == EstadoMesa.DISPONIBLE &&
            it.activo == 1
        }

        llMesasCheckboxes?.removeAllViews()
        checkBoxes.clear()

        mesasDisponiblesEnZona.forEach { mesa ->
            val cb = CheckBox(this).apply {
                text = "${mesa.nombre} (Cap: ${mesa.capacidad} pax)"
                tag = mesa
                setOnCheckedChangeListener { _, _ -> actualizarFiltrosCapacidad() }
            }
            llMesasCheckboxes?.addView(cb)
            checkBoxes.add(cb)
        }
        
        actualizarFiltrosCapacidad()
    }

    private fun actualizarFiltrosCapacidad() {
        val paxStr = spNumeroPersonas?.text.toString()
        val numPersonasRequeridas = paxStr.toIntOrNull() ?: 999

        val seleccionadas = checkBoxes.filter { it.isChecked }.map { it.tag as Mesa }
        val capacidadTotal = seleccionadas.sumOf { it.capacidad }

        if (capacidadTotal >= numPersonasRequeridas && seleccionadas.isNotEmpty()) {
            checkBoxes.filter { !it.isChecked }.forEach { it.isEnabled = false }
        } else {
            checkBoxes.forEach { it.isEnabled = true }
        }
    }
}