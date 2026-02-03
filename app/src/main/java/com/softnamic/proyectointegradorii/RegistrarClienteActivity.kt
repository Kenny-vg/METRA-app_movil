package com.softnamic.proyectointegradorii

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class RegistrarClienteActivity : BaseActivity() {

    private lateinit var spNumeroPersonas: Spinner
    private lateinit var spMesa: Spinner
    private lateinit var spZona: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)
        configurarMenuInferior()

        // SPINNERS
        spNumeroPersonas = findViewById(R.id.spNumeroPersonas)
        spMesa = findViewById(R.id.spMesa)
        spZona = findViewById(R.id.spZona)

        val personas = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val mesas = arrayOf("Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4")
        val zona = arrayOf("Fumadores", "Fiesta", "Otros")

        val adapterPersonas = ArrayAdapter(this, android.R.layout.simple_spinner_item, personas)
        adapterPersonas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spNumeroPersonas.adapter = adapterPersonas

        val adapterMesas = ArrayAdapter(this, android.R.layout.simple_spinner_item, mesas)
        adapterMesas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMesa.adapter = adapterMesas

        val adapterZona = ArrayAdapter(this, android.R.layout.simple_spinner_item, zona)
        adapterZona.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spZona.adapter = adapterZona

    }

}
