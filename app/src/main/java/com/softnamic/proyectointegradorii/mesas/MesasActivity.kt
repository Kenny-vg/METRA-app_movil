package com.softnamic.proyectointegradorii.mesas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity

class MesasActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)
        
        // Le indicamos al menú inferior que estamos en la pantalla de "Mesas"
        configurarMenuInferior(R.id.bottom_tables)

        val rvMesas = findViewById<RecyclerView>(R.id.rvMesas)
        // CORRECCIÓN FINAL Y DEFINITIVA: Usar el ID correcto del Spinner que está en el layout (activity_mesas.xml)
        val spinnerZona = findViewById<Spinner>(R.id.spinnerZona)

        // --- Mock Data (Datos de ejemplo) ---
        val mesasFake = listOf(
            Mesa(1, "Mesa 1", 4, "Fumadores", EstadoMesa.DISPONIBLE),
            Mesa(2, "Mesa 2", 2, "No fumadores", EstadoMesa.OCUPADA),
            Mesa(3, "Mesa 3", 6, "Fumadores", EstadoMesa.DISPONIBLE),
            Mesa(4, "Mesa 4", 8, "Terraza", EstadoMesa.OCUPADA),
            Mesa(5, "Mesa 5", 4, "No fumadores", EstadoMesa.DISPONIBLE)
        )

        rvMesas.layoutManager = LinearLayoutManager(this)
        rvMesas.adapter = MesasAdapter(mesasFake)

        // --- Spinner de ZONAS (Lógica correcta) ---
        val zonas = listOf("Todas", "Fumadores", "No fumadores", "Terraza")
        val zonaAdapter = ArrayAdapter(this, R.layout.spinner_item_custom, zonas)
        zonaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
        spinnerZona.adapter = zonaAdapter
    }
}