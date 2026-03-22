package com.softnamic.proyectointegradorii.core.base

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.inicio.InicioActivity
import com.softnamic.proyectointegradorii.login.LoginActivity
import com.softnamic.proyectointegradorii.mesas.MesasActivity
import com.softnamic.proyectointegradorii.reservas.ReservasActivity
import com.softnamic.proyectointegradorii.wailkin.RegistrarClienteActivity
import com.softnamic.proyectointegradorii.core.data.DataUpdater
import androidx.lifecycle.lifecycleScope
import android.os.Bundle

import com.softnamic.proyectointegradorii.core.data.RestaurantRepository

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Asegurarnos de que el repositorio tenga el token y rol disponibles globalmente
        val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""
        val role = prefs.getString("ROLE", "staff") ?: "staff"
        if (token.isNotEmpty()) {
            RestaurantRepository.currentToken = token
            RestaurantRepository.currentRole = role
        }

        // Iniciamos el actualizador global que corre en background
        DataUpdater.startUpdating()
    }

    // Variables for Navigation Drawer
    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navView: NavigationView
    protected lateinit var toggle: ActionBarDrawerToggle

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

    protected fun configurarToolbarYDrawer() {
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
        val intent = when (menuItem.itemId) {
            R.id.navigation_home -> Intent(this, InicioActivity::class.java)
            R.id.navigation_reservations -> Intent(this, ReservasActivity::class.java)
            R.id.navigation_tables -> Intent(this, MesasActivity::class.java)
            R.id.navigation_profile -> Intent(this, RegistrarClienteActivity::class.java)
            R.id.navigation_logout -> {
                val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
                prefs.edit().clear().apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
                null
            }
            else -> null
        }

        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(it)
            overridePendingTransition(0, 0)
        }

        drawerLayout.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (::toggle.isInitialized && toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}