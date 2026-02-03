package com.softnamic.proyectointegradorii.mesas

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.BaseActivity
import com.softnamic.proyectointegradorii.R

class MesasActivity : BaseActivity() {

    private lateinit var rvMesas: RecyclerView
    private lateinit var spinnerZona: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)
        configurarMenuInferior()

        // 🔹 Inicializar vistas
        rvMesas = findViewById(R.id.rvMesas)
        spinnerZona = findViewById(R.id.spinnerZona)

        // 1️⃣ Datos falsos (mock)
        val mesasFake = listOf(
            Mesa(1, "Mesa 1", 4, "Fumadores", EstadoMesa.DISPONIBLE),
            Mesa(2, "Mesa 2", 2, "No fumadores", EstadoMesa.OCUPADA),
            Mesa(3, "Mesa 3", 6, "Fumadores", EstadoMesa.DISPONIBLE),
            Mesa(4, "Mesa 4", 8, "Terraza", EstadoMesa.OCUPADA),
            Mesa(5, "Mesa 5", 4, "No fumadores", EstadoMesa.DISPONIBLE)
        )

        // 2️⃣ RecyclerView
        rvMesas.layoutManager = LinearLayoutManager(this)
        rvMesas.adapter = MesasAdapter(mesasFake)

        // 3️⃣ Spinner de ZONAS
        val zonas = listOf(
            "Todas",
            "Fumadores",
            "No fumadores",
            "Terraza"
        )

        val zonaAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            zonas
        )
        zonaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerZona.adapter = zonaAdapter
    }
}
