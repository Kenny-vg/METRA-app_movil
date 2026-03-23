package com.softnamic.proyectointegradorii.mesas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var etBuscar: EditText
    private val TAG = "MesasActivity"
    
    private val viewModel: MesasViewModel by viewModels()
    private lateinit var adapter: MesasAdapter
    private var adapterZonas: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesas)

        configurarToolbarYDrawer()
        configurarMenuInferior(R.id.bottom_tables)

        rvMesas = findViewById(R.id.rvMesas)
        spinnerZona = findViewById(R.id.spinnerZona)
        etBuscar = findViewById(R.id.etBuscar)
        rvMesas.layoutManager = LinearLayoutManager(this)
        
        adapter = MesasAdapter { mesa ->
            if (mesa.estado == EstadoMesa.OCUPADA) {
                mostrarDialogoLiberar(mesa)
            } else {
                Toast.makeText(this, "Esta mesa está ${mesa.estado.name.lowercase()}", Toast.LENGTH_SHORT).show()
            }
        }
        rvMesas.adapter = adapter

        configurarBuscador()
        observarViewModel()
        configurarFiltro()
    }

    private fun configurarBuscador() {
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.buscar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun mostrarDialogoLiberar(mesa: Mesa) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("🍽️ Liberar ${mesa.nombre}")
            .setMessage("¿Estás seguro de que quieres liberar esta mesa?")
            .setPositiveButton("Sí, liberar mesa") { _, _ ->
                val ocId = mesa.ocupacionId
                if (ocId != null) {
                    viewModel.finalizarOcupacion(ocId) { exito, msg ->
                        runOnUiThread {
                            if (exito) Toast.makeText(this, "✅ Mesa liberada", Toast.LENGTH_SHORT).show()
                            else Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.zonas.collectLatest { zonas ->
                        if (adapterZonas == null) {
                            adapterZonas = ArrayAdapter(this@MesasActivity, R.layout.spinner_item_custom, zonas.toMutableList())
                            adapterZonas?.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
                            spinnerZona.adapter = adapterZonas
                        } else {
                            adapterZonas?.clear()
                            adapterZonas?.addAll(zonas)
                            adapterZonas?.notifyDataSetChanged()
                        }
                    }
                }
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
