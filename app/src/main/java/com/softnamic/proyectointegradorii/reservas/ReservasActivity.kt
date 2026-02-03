package com.softnamic.proyectointegradorii.reservas

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.BaseActivity
import com.softnamic.proyectointegradorii.R

class ReservasActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)
        configurarMenuInferior()

        // 1. Conectas el RecyclerView
        val recycler = findViewById<RecyclerView>(R.id.rvReservas)

        // 2. Lista de ejemplo (por ahora, sin BD)
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


        // 3. Adapter
        val adapter = ReservaAdapter(listaReservas) { reserva ->
            // 👀 aquí luego abrimos el diálogo
            // por ahora lo dejamos vacío
        }

        // 4. LayoutManager + Adapter
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

}


