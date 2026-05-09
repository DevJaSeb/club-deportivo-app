package com.clubdeportivo.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.models.Vencimiento

class VencimientosAdapter(private val vencimientos: List<Vencimiento>) :
    RecyclerView.Adapter<VencimientosAdapter.VencimientoViewHolder>() {

    class VencimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_item_nombre)
        val tvId: TextView = view.findViewById(R.id.tv_item_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VencimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vencimiento, parent, false)
        return VencimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VencimientoViewHolder, position: Int) {
        val vencimiento = vencimientos[position]
        holder.tvNombre.text = vencimiento.nombre
        holder.tvId.text = vencimiento.idSocio
    }

    override fun getItemCount() = vencimientos.size
}