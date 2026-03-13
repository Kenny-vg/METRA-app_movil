package com.softnamic.proyectointegradorii.inicio

import android.os.Bundle
import android.widget.TextView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class InicioActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        configurarVista()
        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_home)
    }

    private fun configurarVista() {
        val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
        
        val name = prefs.getString("NAME", "") ?: "Usuario"
        val cafeName = prefs.getString("CAFE_NAME", "CAFETERÍA") ?: "CAFETERÍA"

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val tvNombreCafeteria = findViewById<TextView>(R.id.tvNombreCafeteria)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        
        // 1. Nombre de la cafetería
        tvNombreCafeteria.text = cafeName.uppercase()
        
        // 2. Fecha actual formateada
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvFecha.text = sdf.format(Date())

        // 3. Saludo al usuario
        tvBienvenida.text = "¡Bienvenido, $name!"
    }
}
