package com.clubdeportivo.app.adapters
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class PagarCuotaAdapter(
    private val lista: MutableList<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<PagarCuotaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val texto: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = lista[position]

        holder.texto.text = usuario

        holder.itemView.setOnClickListener {
            onClick(usuario)
        }
    }

    fun actualizarLista(nuevaLista: List<String>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}