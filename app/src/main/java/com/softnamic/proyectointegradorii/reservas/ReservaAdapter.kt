package com.softnamic.proyectointegradorii.reservas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R
import androidx.core.content.ContextCompat

class ReservaAdapter(
    private val onClick: (Reserva) -> Unit
) : ListAdapter<Reserva, ReservaAdapter.ViewHolder>(ReservaDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val folio: TextView = view.findViewById(R.id.tvFolio)
        val cliente: TextView = view.findViewById(R.id.tvCliente)
        val fecha: TextView = view.findViewById(R.id.tvFecha)
        val hora: TextView = view.findViewById(R.id.tvHora)
        val personas: TextView = view.findViewById(R.id.tvPersonas)
        val estado: TextView = view.findViewById(R.id.tvEstado)
        val txtPromocion: TextView = view.findViewById(R.id.txtPromocion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = getItem(position)

        holder.folio.text = "Folio: ${reserva.folio}"
        holder.cliente.text = "Cliente: ${reserva.nombreCliente ?: "Desconocido"}"
        holder.fecha.text = "Fecha: ${reserva.fecha}"
        holder.hora.text = "Hora: ${reserva.hora}"
        holder.personas.text = "Personas: ${reserva.personas}"

        val estadoTxt = reserva.estado?.lowercase() ?: "pendiente"
        val ctx = holder.itemView.context
        val card = holder.itemView as com.google.android.material.card.MaterialCardView
        
        when (estadoTxt) {
            "pendiente" -> {
                holder.estado.text = "Estado: Pendiente"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_pendiente_text))
                card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.reserva_pendiente_bg))
            }
            "en_curso" -> {
                holder.estado.text = "Estado: En curso (Cliente en mesa)"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_activa_text))
                card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.reserva_activa_bg))
            }
            "finalizada" -> {
                holder.estado.text = "Estado: Finalizada"
                holder.estado.setTextColor(Color.parseColor("#757575"))
                card.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
            }
            "cancelada" -> {
                holder.estado.text = "Estado: Cancelada"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_cancelada_text))
                card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.reserva_cancelada_bg))
            }
            "no_show" -> {
                holder.estado.text = "Estado: No se presentó"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_noshow_text))
                card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.reserva_noshow_bg))
            }
            else -> {
                holder.estado.text = "Estado: ${reserva.estado?.replaceFirstChar { it.uppercase() } ?: "Pendiente"}"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_secondary))
                card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.coffee_surface))
            }
        }

        holder.txtPromocion.text = reserva.promocion
        if (reserva.promocion == "Sin promoción") {
            holder.txtPromocion.visibility = View.GONE
        } else {
            holder.txtPromocion.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            onClick(reserva)
        }
    }
}

class ReservaDiffCallback : DiffUtil.ItemCallback<Reserva>() {
    override fun areItemsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
        // En DataClasses, == compara todos los campos
        return oldItem == newItem
    }
}
