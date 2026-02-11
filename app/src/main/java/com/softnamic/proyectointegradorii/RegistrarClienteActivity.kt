package com.softnamic.proyectointegradorii

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class RegistrarClienteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)

        configurarMenuInferior(R.id.bottom_profile)

        val spNumeroPersonas = findViewById<AutoCompleteTextView>(R.id.spNumeroPersonas)
        val spZona = findViewById<AutoCompleteTextView>(R.id.spZona)
        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar)
        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)

        val adapterPersonas = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, (1..10).map { it.toString() })
        spNumeroPersonas.setAdapter(adapterPersonas)

        val adapterZonas = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listOf("Fumadores", "No fumadores", "Terraza"))
        spZona.setAdapter(adapterZonas)

        btnConfirmar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val personas = spNumeroPersonas.text.toString()
            val zona = spZona.text.toString()

            if (nombre.isEmpty()) {
                etNombre.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            if (personas.isEmpty() || personas.toInt() <= 0) {
                spNumeroPersonas.error = "Debe seleccionar un número de personas mayor a 0"
                return@setOnClickListener
            }

            if (zona.isEmpty()) {
                spZona.error = "Debe seleccionar una zona"
                return@setOnClickListener
            }

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            // Aquí iría la lógica para guardar el registro
            finish()
        }
    }
}
