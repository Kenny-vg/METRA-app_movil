package com.softnamic.proyectointegradorii.mesas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R
import com.softnamic.proyectointegradorii.mesas.EstadoMesa
import com.softnamic.proyectointegradorii.mesas.Mesa

class MesasAdapter(
    private val mesas: List<Mesa>
) : RecyclerView.Adapter<MesasAdapter.MesaViewHolder>() {

    inner class MesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvMesaNombre)
        val tvCapacidad: TextView = itemView.findViewById(R.id.tvMesaCapacidad)
        val tvEstado: TextView = itemView.findViewById(R.id.tvMesaEstado)
        val spinnerAccion: Spinner = itemView.findViewById(R.id.spinnerAccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesas, parent, false)
        return MesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = mesas[position]

        holder.tvNombre.text = mesa.nombre
        holder.tvCapacidad.text = "Capacidad: ${mesa.capacidad} personas"

        when (mesa.estado) {
            EstadoMesa.DISPONIBLE -> {
                holder.tvEstado.text = "Disponible"
                holder.tvEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.status_available
                    )
                )

                setSpinner(holder.spinnerAccion, holder.itemView, "Asignar")
            }

            EstadoMesa.OCUPADA -> {
                holder.tvEstado.text = "Ocupada"
                holder.tvEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.status_occupied
                    )
                )

                setSpinner(holder.spinnerAccion, holder.itemView, "Liberar")
            }
        }
    }

    override fun getItemCount(): Int = mesas.size

    private fun setSpinner(spinner: Spinner, view: View, accion: String) {
        val adapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            listOf(accion)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
