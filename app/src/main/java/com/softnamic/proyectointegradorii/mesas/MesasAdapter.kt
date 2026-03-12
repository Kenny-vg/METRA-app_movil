package com.softnamic.proyectointegradorii.mesas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.content.res.ColorStateList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R

class MesasAdapter : ListAdapter<Mesa, MesasAdapter.MesaViewHolder>(MesaDiffCallback()) {

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
        val mesa = getItem(position)

        holder.tvNombre.text = mesa.nombre
        holder.tvCapacidad.text = "Capacidad: ${mesa.capacidad} personas"

        if (mesa.activo == 0) {
            holder.tvEstado.text = "Desactivada"
            holder.tvEstado.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E")) // Gris
            
            // Ocultar o deshabilitar dropdown si está desactivada
            holder.spinnerAccion.isEnabled = false
            setSpinner(holder.spinnerAccion, holder.itemView, "No disponible")
            
            // Opcional: Hacer toda la tarjeta opaca/gris
            (holder.itemView as? com.google.android.material.card.MaterialCardView)?.setCardBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"))
        } else {
            // Restaurar fondo blanco por si era reciclada
            (holder.itemView as? com.google.android.material.card.MaterialCardView)?.setCardBackgroundColor(android.graphics.Color.WHITE)
            holder.spinnerAccion.isEnabled = true

            when (mesa.estado) {
                EstadoMesa.DISPONIBLE -> {
                    holder.tvEstado.text = "Disponible"
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.status_available
                        )
                    )

                    setSpinner(holder.spinnerAccion, holder.itemView, "Asignar")
                }

                EstadoMesa.OCUPADA -> {
                    holder.tvEstado.text = "Ocupada"
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.status_occupied
                        )
                    )

                    setSpinner(holder.spinnerAccion, holder.itemView, "Liberar")
                }

                EstadoMesa.RESERVADA -> {
                    holder.tvEstado.text = "Reservada"
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.status_reserved
                        )
                    )

                    setSpinner(holder.spinnerAccion, holder.itemView, "Ver Detalles")
                }
            }
        }
    }

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

class MesaDiffCallback : DiffUtil.ItemCallback<Mesa>() {
    override fun areItemsTheSame(oldItem: Mesa, newItem: Mesa): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Mesa, newItem: Mesa): Boolean {
        // En DataClasses, == compara todos los campos
        return oldItem == newItem
    }
}
