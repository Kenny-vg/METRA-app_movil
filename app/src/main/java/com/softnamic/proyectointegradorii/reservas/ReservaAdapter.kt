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
        val tvZona: TextView = view.findViewById(R.id.tvZona)
        val viewEstadoBar = view.findViewById<View>(R.id.viewEstadoBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = getItem(position)
        val ctx = holder.itemView.context

        // Header
        holder.cliente.text = reserva.nombreCliente ?: "Cliente desconocido"
        holder.folio.text = reserva.folio

        // Body sin emojis, los iconos ahora están en el XML
        holder.fecha.text = reserva.fecha
        holder.hora.text = reserva.hora
        holder.personas.text = "${reserva.personas} personas"
        holder.tvZona.text = reserva.zona ?: "Sin zona"

        // Badge de estado y barra de color
        val estadoTxt = reserva.estado?.lowercase() ?: "pendiente"
        val card = holder.itemView as com.google.android.material.card.MaterialCardView

        // Reset card color
        card.setCardBackgroundColor(Color.WHITE)

        when (estadoTxt) {
            "pendiente" -> {
                holder.estado.text = "PENDIENTE"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_pendiente_text))
                val bgColor = ContextCompat.getColor(ctx, R.color.reserva_pendiente_bg)
                val barColor = ContextCompat.getColor(ctx, R.color.reserva_pendiente_text)
                holder.estado.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                holder.viewEstadoBar.setBackgroundColor(barColor)
            }
            "en_curso" -> {
                holder.estado.text = "EN CURSO"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_activa_text))
                val bgColor = ContextCompat.getColor(ctx, R.color.reserva_activa_bg)
                val barColor = ContextCompat.getColor(ctx, R.color.reserva_activa_text)
                holder.estado.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                holder.viewEstadoBar.setBackgroundColor(barColor)
            }
            "finalizada" -> {
                holder.estado.text = "FINALIZADA"
                holder.estado.setTextColor(Color.parseColor("#757575"))
                val bgColor = Color.parseColor("#E0E0E0")
                val barColor = Color.parseColor("#9E9E9E")
                holder.estado.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                holder.viewEstadoBar.setBackgroundColor(barColor)
                card.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
            }
            "cancelada" -> {
                holder.estado.text = "CANCELADA"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_cancelada_text))
                val bgColor = ContextCompat.getColor(ctx, R.color.reserva_cancelada_bg)
                val barColor = ContextCompat.getColor(ctx, R.color.reserva_cancelada_text)
                holder.estado.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                holder.viewEstadoBar.setBackgroundColor(barColor)
                card.setCardBackgroundColor(Color.parseColor("#FFF5F5"))
            }
            "no_show" -> {
                holder.estado.text = "NO SHOW"
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.reserva_noshow_text))
                val bgColor = ContextCompat.getColor(ctx, R.color.reserva_noshow_bg)
                val barColor = ContextCompat.getColor(ctx, R.color.reserva_noshow_text)
                holder.estado.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                holder.viewEstadoBar.setBackgroundColor(barColor)
                card.setCardBackgroundColor(Color.parseColor("#FFF5F5"))
            }
            else -> {
                holder.estado.text = estadoTxt.uppercase()
                holder.estado.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_secondary))
                val bgColor = Color.parseColor("#F0F0F0")
                val barColor = Color.parseColor("#9E9E9E")
                holder.estado.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                holder.viewEstadoBar.setBackgroundColor(barColor)
            }
        }

        // Promoción
        if (reserva.promocion == "Sin promoción" || reserva.promocion.isNullOrBlank()) {
            holder.txtPromocion.visibility = View.GONE
        } else {
            holder.txtPromocion.text = "PROMOCIÓN: ${reserva.promocion}"
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
        return oldItem == newItem
    }
}
