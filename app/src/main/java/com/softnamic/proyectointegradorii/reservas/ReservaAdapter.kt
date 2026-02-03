package com.softnamic.proyectointegradorii.reservas

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

        holder.itemView.setOnClickListener {
            onClick(reserva)
        }
    }

    override fun getItemCount() = lista.size
}
