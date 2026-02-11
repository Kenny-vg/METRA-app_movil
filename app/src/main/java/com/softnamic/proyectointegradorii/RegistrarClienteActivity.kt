package com.softnamic.proyectointegradorii

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class RegistrarClienteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)

        // Le indicamos al menú inferior que estamos en la pantalla de "Perfil" (assumed context)
        // Adjust if necessary based on actual tab index, but keeping as is for now
        configurarMenuInferior(R.id.bottom_profile)

        // DROPDOWNS (Exposed Dropdown Menus)
        val spNumeroPersonas = findViewById<AutoCompleteTextView>(R.id.spNumeroPersonas)
        val spMesa = findViewById<AutoCompleteTextView>(R.id.spMesa)
        val spZona = findViewById<AutoCompleteTextView>(R.id.spZona)

        val personas = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val mesas = arrayOf("Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4")
        val zona = arrayOf("Fumadores", "Fiesta", "Otros")

        // Adapters for AutoCompleteTextView
        val adapterPersonas = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, personas)
        spNumeroPersonas.setAdapter(adapterPersonas)

        val adapterMesas = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mesas)
        spMesa.setAdapter(adapterMesas)

        val adapterZona = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, zona)
        spZona.setAdapter(adapterZona)
    }
}