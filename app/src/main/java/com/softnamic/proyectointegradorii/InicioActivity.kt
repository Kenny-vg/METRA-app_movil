package com.softnamic.proyectointegradorii

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class InicioActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        configurarMenuInferior()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white, theme)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_reservations -> {
                    Toast.makeText(this, "Aquí irá ReservasActivity", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_tables -> {
                    Toast.makeText(this, "Aquí irá MesasActivity", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_profile -> {
                    Toast.makeText(this, "Aquí irá RegistrarClienteActivity", Toast.LENGTH_SHORT).show()
                }
                R.id.navigation_logout -> {
                    Toast.makeText(this, "Cerrar sesión", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

