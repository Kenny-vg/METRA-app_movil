package com.softnamic.proyectointegradorii.mesas

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.core.network.RetrofitClient
import kotlinx.coroutines.launch

class MesasActivity : BaseActivity() {

    private var listaMesasCompleta: List<Mesa> = emptyList()
    private lateinit var rvMesas: RecyclerView
    private lateinit var spinnerZona: Spinner
    private val TAG = "MesasActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)

        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_tables)

        rvMesas = findViewById(R.id.rvMesas)
        spinnerZona = findViewById(R.id.spinnerZona)
        rvMesas.layoutManager = LinearLayoutManager(this)

        cargarDatos()
    }

    private fun cargarDatos() {
        val prefs = getSharedPreferences("MY_APP", MODE_PRIVATE)
        val token = prefs.getString("TOKEN", "") ?: ""
        
        // Verificamos si hay token
        if (token.isEmpty()) {
            Toast.makeText(this, "No hay token de sesión. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        val authHeader = "Bearer $token"
        Log.d(TAG, "Enviando Token: $authHeader")

        lifecycleScope.launch {
            try {
                // Intentamos cargar Zonas
                val resZonas = RetrofitClient.instance.getZonas(authHeader)
                if (resZonas.isSuccessful) {
                    val zonas = resZonas.body()?.data ?: emptyList()
                    val nombres = mutableListOf("Todas")
                    nombres.addAll(zonas.map { it.nombre_zona })
                    
                    val adapter = ArrayAdapter(this@MesasActivity, R.layout.spinner_item_custom, nombres)
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
                    spinnerZona.adapter = adapter
                }

                // Intentamos cargar Mesas
                val resMesas = RetrofitClient.instance.getMesas(authHeader)
                if (resMesas.isSuccessful) {
                    val mesasApi = resMesas.body()?.data ?: emptyList()
                    listaMesasCompleta = mesasApi.map {
                        Mesa(it.id, "Mesa ${it.numero_mesa}", it.capacidad, it.zona.nombre_zona, EstadoMesa.DISPONIBLE)
                    }
                    rvMesas.adapter = MesasAdapter(listaMesasCompleta)
                    configurarFiltro()
                    Log.d(TAG, "Mesas cargadas con éxito")
                } else {
                    // AQUÍ VERÁS EL ERROR REAL EN TU CELULAR
                    val errorCode = resMesas.code() // Ej: 401, 403, 404
                    val errorMsg = resMesas.errorBody()?.string() ?: "Error desconocido"
                    Log.e(TAG, "Error $errorCode: $errorMsg")
                    
                    Toast.makeText(this@MesasActivity, "Error $errorCode: $errorMsg", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Excepción: ${e.message}")
                Toast.makeText(this@MesasActivity, "Fallo de red: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun configurarFiltro() {
        spinnerZona.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val zona = p0?.getItemAtPosition(pos).toString()
                val filtradas = if (zona == "Todas") listaMesasCompleta else listaMesasCompleta.filter { it.zona == zona }
                rvMesas.adapter = MesasAdapter(filtradas)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}
