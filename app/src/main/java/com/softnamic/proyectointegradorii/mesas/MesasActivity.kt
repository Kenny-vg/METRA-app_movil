package com.softnamic.proyectointegradorii.mesas

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.core.base.BaseActivity
import com.softnamic.proyectointegradorii.core.data.RestaurantRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MesasActivity : BaseActivity() {

    private lateinit var rvMesas: RecyclerView
    private lateinit var spinnerZona: Spinner
    private val TAG = "MesasActivity"
    
    private val viewModel: MesasViewModel by viewModels()
    private lateinit var adapter: MesasAdapter
    
    // Guardamos el adapter de zonas para actualizarlo
    private var adapterZonas: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)

        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_tables)

        rvMesas = findViewById(R.id.rvMesas)
        spinnerZona = findViewById(R.id.spinnerZona)
        rvMesas.layoutManager = LinearLayoutManager(this)
        
        adapter = MesasAdapter()
        rvMesas.adapter = adapter

        observarViewModel()
        configurarFiltro()
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar Zonas
                launch {
                    viewModel.zonas.collectLatest { zonas ->
                        Log.d(TAG, "Zonas recibidas en UI: ${zonas.size}")
                        
                        // Si el adapter ya existe, lo limpiamos y actualizamos
                        if (adapterZonas == null) {
                            adapterZonas = ArrayAdapter(this@MesasActivity, R.layout.spinner_item_custom, zonas.toMutableList())
                            adapterZonas?.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
                            spinnerZona.adapter = adapterZonas
                        } else {
                            // Actualizar la lista interna del adapter sin recrearlo
                            adapterZonas?.clear()
                            adapterZonas?.addAll(zonas)
                            adapterZonas?.notifyDataSetChanged()
                        }
                        
                        // Mantener la selección actual
                        val currentSelection = viewModel.zonaSeleccionada.value
                        var pos = zonas.indexOf(currentSelection)
                        
                        // Si no lo encuentra, intentar buscar sin la etiqueta (SUSPENDIDA)
                        if (pos < 0 && currentSelection != "Todas") {
                            val currentLimpia = currentSelection.replace(" (SUSPENDIDA)", "")
                            pos = zonas.indexOfFirst { it.replace(" (SUSPENDIDA)", "") == currentLimpia }
                            
                            // Si lo encontramos con otro nombre (ej. pasó a estar suspendida), 
                            // actualizamos el viewModel para que coincida exactamente
                            if (pos >= 0) {
                                viewModel.seleccionarZona(zonas[pos])
                            }
                        }

                        if (pos >= 0) {
                            spinnerZona.setSelection(pos, false)
                        }
                    }
                }
                
                // Observar Mesas
                launch {
                    viewModel.mesasFiltradas.collectLatest { mesas ->
                        adapter.submitList(mesas)
                    }
                }
            }
        }
    }

    private fun configurarFiltro() {
        spinnerZona.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val zona = p0?.getItemAtPosition(pos).toString()
                viewModel.seleccionarZona(zona)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}
