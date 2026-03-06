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
}