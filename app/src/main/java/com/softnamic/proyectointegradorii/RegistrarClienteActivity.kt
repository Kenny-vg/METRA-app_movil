package com.softnamic.proyectointegradorii

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class RegistrarClienteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_cliente)

        // Le indicamos al menú inferior que estamos en la pantalla de "Perfil"
        configurarMenuInferior(R.id.bottom_profile)

        // SPINNERS
        val spNumeroPersonas = findViewById<Spinner>(R.id.spNumeroPersonas)
        val spMesa = findViewById<Spinner>(R.id.spMesa)
        val spZona = findViewById<Spinner>(R.id.spZona)

        val personas = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val mesas = arrayOf("Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4")
        val zona = arrayOf("Fumadores", "Fiesta", "Otros")

        // Usamos los adapters personalizados para mantener la consistencia
        val adapterPersonas = ArrayAdapter(this, R.layout.spinner_item_custom, personas)
        adapterPersonas.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
        spNumeroPersonas.adapter = adapterPersonas

        val adapterMesas = ArrayAdapter(this, R.layout.spinner_item_custom, mesas)
        adapterMesas.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
        spMesa.adapter = adapterMesas

        val adapterZona = ArrayAdapter(this, R.layout.spinner_item_custom, zona)
        adapterZona.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
        spZona.adapter = adapterZona
    }
}