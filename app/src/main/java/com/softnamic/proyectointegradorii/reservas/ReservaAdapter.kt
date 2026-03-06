package com.softnamic.proyectointegradorii.reservas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.softnamic.proyectointegradorii.R

class ReservaAdapter(
    private val lista: List<Reserva>,
    private val onClick: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val mesa: TextView = view.findViewById(R.id.tvMesa)
        val zona: TextView = view.findViewById(R.id.tvZona)
        val estado: TextView = view.findViewById(R.id.tvEstado)
        val txtPromocion: TextView = view.findViewById(R.id.txtPromocion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reserva = lista[position]

        holder.nombre.text = "${reserva.nombre} - ${reserva.hora}"
        holder.mesa.text = reserva.mesa ?: "Sin mesa asignada"
        holder.zona.text = "Zona: ${reserva.zona}"

        when (reserva.estado) {
            "Llegó" -> {
                holder.estado.text = "Llegó"
                holder.estado.setTextColor(Color.parseColor("#388E3C")) // Dark green
                (holder.itemView as com.google.android.material.card.MaterialCardView).setCardBackgroundColor(Color.parseColor("#E8F5E9")) // Very light green
            }
            "No llegó" -> {
                holder.estado.text = "No llegó"
                holder.estado.setTextColor(Color.parseColor("#D32F2F")) // Dark red
                (holder.itemView as com.google.android.material.card.MaterialCardView).setCardBackgroundColor(Color.parseColor("#FFEBEE")) // Very light red
            }
            else -> {
                holder.estado.text = "Pendiente"
                holder.estado.setTextColor(Color.parseColor("#6B6B6B"))
                (holder.itemView as com.google.android.material.card.MaterialCardView).setCardBackgroundColor(Color.WHITE)
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

    override fun getItemCount() = lista.size
}
