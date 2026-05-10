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
import com.clubdeportivo.app.enums.FormaDePago
import com.clubdeportivo.app.enums.Actividades
import com.clubdeportivo.app.R
import com.clubdeportivo.app.adapters.PagarCuotaAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class PagarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnRegistrarPago = findViewById<Button>(R.id.btn_registrar_pago)
        val etActividades = findViewById<MaterialAutoCompleteTextView>(R.id.et_actividades)
        val etFormaDePago = findViewById<MaterialAutoCompleteTextView>(R.id.et_forma_de_pago)
        // Searchbar Usuarios Activos
        val searchBar = findViewById<SearchBar>(R.id.sb_usuarios)
        val searchView = findViewById<SearchView>(R.id.sv_usuarios)
        val recycler = findViewById<RecyclerView>(R.id.rv_usuarios)
        // Enums
        val actividades = Actividades.entries.map { it.texto }
        val formaDePago = FormaDePago.entries.map { it.texto }


        searchView.setupWithSearchBar(searchBar)
        // VALORES DE PRUEBA TEMPORALES (borrar)
        val listaPrueba = listOf("Juan Santos", "Pedro Arias", "María Marta", "Javier Ojeda")

        val adapterPagarCuota = PagarCuotaAdapter(listaPrueba.toMutableList()) { seleccionado ->
            searchBar.setText(seleccionado)
            searchView.hide()
        }
        recycler.adapter = adapterPagarCuota
        recycler.layoutManager = LinearLayoutManager(this)

        // Filtrado en vivo - usuarios activos
        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtrada = listaPrueba.filter {
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

        // Redireccionar desde pagar cuota a comprobante
        btnRegistrarPago.setOnClickListener {
            val intent = Intent(this, ComprobanteActivity::class.java)
            startActivity(intent)
        }

        // Menú vertical de actividades
        val adapterActividades = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            actividades
        )

        etActividades.setAdapter(adapterActividades)

        // Menú vertical de Forma de pago
        val adapterFormaDePago = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            formaDePago
        )

        etFormaDePago.setAdapter(adapterFormaDePago)
    }
}