package com.clubdeportivo.app.ui

import android.text.Editable
import android.text.TextWatcher
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Button
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.adapters.PagarCuotaAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class PagarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnRegistrarPago = findViewById<Button>(R.id.btn_registrar_pago)
        // Searchbar Usuarios Activos
        val searchBar = findViewById<SearchBar>(R.id.sb_usuarios)
        val searchView = findViewById<SearchView>(R.id.sv_usuarios)
        val recycler = findViewById<RecyclerView>(R.id.rv_usuarios)

        searchView.setupWithSearchBar(searchBar)

        val listaOriginal = listOf("Juan Santos", "Pedro Arias", "María Marta", "Javier Ojeda")

        val adapterPagarCuota = PagarCuotaAdapter(listaOriginal.toMutableList()) { seleccionado ->
            searchBar.setText(seleccionado)
            searchView.hide()
        }
        recycler.adapter = adapterPagarCuota
        recycler.layoutManager = LinearLayoutManager(this)

        // Filtrado en vivo - usuarios activos
        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtrada = listaOriginal.filter {
                    it.contains(s.toString(), ignoreCase = true)
                }
                adapterPagarCuota.actualizarLista(filtrada)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        btnRegistrarPago.setOnClickListener {
            val intent = Intent(this, ComprobanteActivity::class.java)
            startActivity(intent)
        }

        // Menú vertical de actividades
        val actividades = arrayOf(
            "Natación",
            "Elongación",
            "Tenis",
            "Yoga",
            "Musculación",
            "Artes Marciales"
        )

        val etActividades =
            findViewById<MaterialAutoCompleteTextView>(R.id.et_actividades)

        val adapterActividades = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            actividades
        )

        etActividades.setAdapter(adapterActividades)
    }
}