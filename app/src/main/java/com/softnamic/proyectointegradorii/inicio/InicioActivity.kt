package com.softnamic.proyectointegradorii.inicio

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.login.LoginActivity
import com.softnamic.proyectointegradorii.mesas.MesasActivity
import com.softnamic.proyectointegradorii.reservas.ReservasActivity
import com.softnamic.proyectointegradorii.wailkin.RegistrarClienteActivity
import kotlin.jvm.java

class InicioActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        configurarSaludo()
        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_home)
    }

    private fun configurarSaludo() {

        val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
        val name = prefs.getString("NAME", "") ?: ""

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        
        tvBienvenida.text = "Bienvenido, ${name.ifEmpty { "Usuario" }}"
    }

    private fun configurarToolbarYDrawer() {

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white, theme)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            manejarDrawer(menuItem)
            true
        }
    }

    private fun manejarDrawer(menuItem: MenuItem) {

        when (menuItem.itemId) {

            R.id.navigation_home -> {
                // Ya estamos aquí
            }

            R.id.navigation_reservations -> {
                startActivity(Intent(this, ReservasActivity::class.java))
            }

            R.id.navigation_tables -> {
                startActivity(Intent(this, MesasActivity::class.java))
            }

            R.id.navigation_profile -> {
                startActivity(Intent(this, RegistrarClienteActivity::class.java))
            }

            R.id.navigation_logout -> {

                val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
                prefs.edit().clear().apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }

        drawerLayout.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}