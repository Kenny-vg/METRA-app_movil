package com.softnamic.proyectointegradorii

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.softnamic.proyectointegradorii.mesas.MesasActivity
import com.softnamic.proyectointegradorii.reservas.ReservasActivity

open class BaseActivity : AppCompatActivity() {

    // 🔹 CONTROLAR SI ESTA PANTALLA TIENE MENÚ DE 3 PUNTITOS
    open fun mostrarMenuOverflow(): Boolean = true

    // ---------------- MENÚ INFERIOR ----------------
    fun configurarMenuInferior() {

        findViewById<ImageView>(R.id.inicio)?.setOnClickListener {
            startActivity(Intent(this, InicioActivity::class.java))
        }

        findViewById<ImageView>(R.id.reservas)?.setOnClickListener {
            startActivity(Intent(this, ReservasActivity::class.java))
        }

        findViewById<ImageView>(R.id.mesas)?.setOnClickListener {
            startActivity(Intent(this, MesasActivity::class.java))
        }

        findViewById<ImageView>(R.id.usuario)?.setOnClickListener {
            startActivity(Intent(this, RegistrarClienteActivity::class.java))
        }
    }

    // ---------------- MENÚ 3 PUNTITOS ----------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!mostrarMenuOverflow()) return false
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.navigation_home -> {
                startActivity(Intent(this, InicioActivity::class.java))
                return true
            }

            R.id.navigation_reservations -> {
                startActivity(Intent(this, ReservasActivity::class.java))
                return true
            }

            R.id.navigation_tables -> {
                startActivity(Intent(this, MesasActivity::class.java))
                return true
            }

            R.id.navigation_profile -> {
                startActivity(Intent(this, RegistrarClienteActivity::class.java))
                return true
            }

            R.id.navigation_logout -> {
                cerrarSesion()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cerrarSesion() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}