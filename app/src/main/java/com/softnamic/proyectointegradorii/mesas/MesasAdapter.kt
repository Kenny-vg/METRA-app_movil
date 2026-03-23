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
        val viewEstadoBar: View = itemView.findViewById(R.id.viewEstadoBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesas, parent, false)
        return MesaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        val mesa = getItem(position)
        val ctx = holder.itemView.context
        val card = holder.itemView as com.google.android.material.card.MaterialCardView

        holder.tvNombre.text = mesa.nombre
        holder.tvCapacidad.text = "Hasta ${mesa.capacidad} personas"
        
        val nombreZonaLimpio = mesa.zona.replace(" (SUSPENDIDA)", "", ignoreCase = true)
        holder.tvZona.text = nombreZonaLimpio

        if (mesa.activo == 0) {
            // Desactivada
            holder.tvEstado.text = "DESACTIVADA"
            holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            holder.tvEstado.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E"))
            holder.viewEstadoBar.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E"))
            card.setCardBackgroundColor(android.graphics.Color.parseColor("#F0F0F0"))
            card.alpha = 0.7f
        } else {
            card.alpha = 1.0f
            when (mesa.estado) {
                EstadoMesa.DISPONIBLE -> {
                    holder.tvEstado.text = "DISPONIBLE"
                    holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.status_available))
                    holder.viewEstadoBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_available))
                    card.setCardBackgroundColor(android.graphics.Color.WHITE)
                }
                EstadoMesa.OCUPADA -> {
                    holder.tvEstado.text = "OCUPADA"
                    holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.status_occupied))
                    holder.viewEstadoBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_occupied))
                    card.setCardBackgroundColor(android.graphics.Color.parseColor("#FFF8F6"))
                }
                EstadoMesa.RESERVADA -> {
                    holder.tvEstado.text = "RESERVADA"
                    holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
                    holder.tvEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.status_reserved))
                    holder.viewEstadoBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.status_reserved))
                    card.setCardBackgroundColor(android.graphics.Color.parseColor("#FFFBF0"))
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
