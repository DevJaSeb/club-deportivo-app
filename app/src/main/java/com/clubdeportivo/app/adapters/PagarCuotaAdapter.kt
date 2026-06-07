package com.clubdeportivo.app.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R   // ← asegúrate de tener el layout item_usuario.xml

data class UsuarioActivo(
    val nombre: String,
    val dni: String,
    val tipo: String   // "Socio" o "No Socio"
)
class PagarCuotaAdapter(
    private val lista: MutableList<UsuarioActivo>,
    private val onClick: (UsuarioActivo) -> Unit   // ahora pasa el objeto completo
) : RecyclerView.Adapter<PagarCuotaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre)
        val tvDni: TextView = view.findViewById(R.id.tv_dni)
        val tvTipo: TextView = view.findViewById(R.id.tv_tipo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_activo, parent, false)  // ← layout personalizado
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = lista[position]
        holder.tvNombre.text = usuario.nombre
        holder.tvDni.text = "DNI: ${usuario.dni}"
        holder.tvTipo.text = usuario.tipo   // "Socio" o "No Socio"
        holder.itemView.setOnClickListener {
            onClick(usuario)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarLista(nuevaLista: List<UsuarioActivo>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}