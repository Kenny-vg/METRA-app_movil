package com.softnamic.proyectointegradorii.mesas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.content.res.ColorStateList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R

class MesasAdapter(
    private val onMesaClick: ((Mesa) -> Unit)? = null
) : ListAdapter<Mesa, MesasAdapter.MesaViewHolder>(MesaDiffCallback()) {

    inner class MesaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvMesaNombre)
        val tvCapacidad: TextView = itemView.findViewById(R.id.tvMesaCapacidad)
        val tvZona: TextView = itemView.findViewById(R.id.tvMesaZona)
        val tvEstado: TextView = itemView.findViewById(R.id.tvMesaEstado)
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
        
        val nombreZonaLimpio = mesa.zona.replace(" (SUSPENDIDA)", "", ignoreCase = true)
        holder.tvZona.text = "\uD83D\uDCCD Zona: $nombreZonaLimpio"

        if (mesa.activo == 0) {
            holder.tvEstado.text = "Desactivada"
            holder.tvEstado.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E"))
            (holder.itemView as? com.google.android.material.card.MaterialCardView)?.setCardBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"))
        } else {
            when (mesa.estado) {
                EstadoMesa.DISPONIBLE -> {
                    holder.tvEstado.text = "Disponible"
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.status_available))
                    (holder.itemView as? com.google.android.material.card.MaterialCardView)?.setCardBackgroundColor(android.graphics.Color.WHITE)
                }
                EstadoMesa.OCUPADA -> {
                    holder.tvEstado.text = "Ocupada"
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.status_occupied))
                    (holder.itemView as? com.google.android.material.card.MaterialCardView)?.setCardBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"))
                }
                EstadoMesa.RESERVADA -> {
                    holder.tvEstado.text = "Reservada"
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.status_reserved))
                    (holder.itemView as? com.google.android.material.card.MaterialCardView)?.setCardBackgroundColor(android.graphics.Color.WHITE)
                }
            }
        }
        
        holder.itemView.setOnClickListener {
            onMesaClick?.invoke(mesa)
        }
    }
}

class MesaDiffCallback : DiffUtil.ItemCallback<Mesa>() {
    override fun areItemsTheSame(oldItem: Mesa, newItem: Mesa): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Mesa, newItem: Mesa): Boolean = oldItem == newItem
}
