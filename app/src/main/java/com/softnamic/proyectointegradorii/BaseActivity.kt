package com.softnamic.proyectointegradorii

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.softnamic.proyectointegradorii.mesas.MesasActivity
import com.softnamic.proyectointegradorii.reservas.ReservasActivity

open class BaseActivity : AppCompatActivity() {

    // Variable para guardar el ID del item del menú para ESTA activity en particular
    private var navItemId: Int = 0

    /**
     * onResume() se llama cada vez que la activity pasa a primer plano.
     * (Al crearla por primera vez o al volver a ella con el botón de retroceso).
     * Aquí nos aseguramos de que el botón correcto esté seleccionado.
     */
    override fun onResume() {
        super.onResume()
        if (navItemId != 0) {
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.selectedItemId = navItemId
        }
    }

    fun configurarMenuInferior(selectedItemId: Int) {
        // 1. Guardamos cuál es el item de esta Activity
        navItemId = selectedItemId

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 2. Establecemos el listener para manejar los clics
        bottomNavigationView.setOnItemSelectedListener { item ->
            // Si el usuario hace clic en el mismo ítem donde ya está, no hacemos nada.
            if (item.itemId == selectedItemId) {
                return@setOnItemSelectedListener true
            }

            val intent = when (item.itemId) {
                R.id.bottom_home -> Intent(this, InicioActivity::class.java)
                R.id.bottom_reservations -> Intent(this, ReservasActivity::class.java)
                R.id.bottom_tables -> Intent(this, MesasActivity::class.java)
                R.id.bottom_profile -> Intent(this, RegistrarClienteActivity::class.java)
                else -> null
            }

            intent?.let {
                // 3. Usamos REORDER_TO_FRONT para una navegación eficiente
                it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(it)
                // 4. Anulamos las animaciones para una transición limpia
                overridePendingTransition(0, 0)
            }
            true
        }
    }
}
