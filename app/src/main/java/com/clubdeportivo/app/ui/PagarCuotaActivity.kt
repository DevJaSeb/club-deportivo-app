package com.clubdeportivo.app.ui

import android.text.Editable
import android.text.TextWatcher
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.enums.FormaDePago
import com.clubdeportivo.app.R
import com.clubdeportivo.app.adapters.PagarCuotaAdapter
import com.clubdeportivo.app.adapters.UsuarioActivo
import com.clubdeportivo.app.db.DBClub
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class PagarCuotaActivity : AppCompatActivity() {
    private lateinit var db: DBClub
    private lateinit var adapterPagarCuota: PagarCuotaAdapter
    private val listaUsuariosCompleta = mutableListOf<UsuarioActivo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        db = DBClub(this)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnRegistrarPago = findViewById<Button>(R.id.btn_registrar_pago)
        val tilActividades = findViewById<TextInputLayout>(R.id.til_actividades)
        val etActividades = findViewById<MaterialAutoCompleteTextView>(R.id.et_actividades)
        val etFormaDePago = findViewById<MaterialAutoCompleteTextView>(R.id.et_forma_de_pago)
        // Searchbar Usuarios Activos
        val searchBar = findViewById<SearchBar>(R.id.sb_usuarios)
        val searchView = findViewById<SearchView>(R.id.sv_usuarios)
        val recycler = findViewById<RecyclerView>(R.id.rv_usuarios)
        // Enum forma de pago
        val formaDePago = FormaDePago.entries.map { it.texto }


        searchView.setupWithSearchBar(searchBar)
        // VALORES DE PRUEBA TEMPORALES (borrar)
        val listaPrueba = listOf(
            UsuarioActivo("Juan Pérez", "12345678"),
            UsuarioActivo("Ana Gómez", "87654321"),
            UsuarioActivo("Carlos Ruiz", "11223344")
        )
        listaUsuariosCompleta.addAll(listaPrueba)

        // adaptador searchbar
        adapterPagarCuota = PagarCuotaAdapter(listaPrueba.toMutableList()) { seleccionado ->
            // Convertir el objeto a string para mostrar en la SearchBar
            searchBar.setText("${seleccionado.nombre} (DNI: ${seleccionado.dni})")
            searchView.hide()
        }

        recycler.adapter = adapterPagarCuota
        recycler.layoutManager = LinearLayoutManager(this)

        // Filtrado en vivo - usuarios activos
        searchView.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s?.toString() ?: ""
                val filtrada = if (texto.isEmpty()) {
                    listaUsuariosCompleta
                } else {
                    listaUsuariosCompleta.filter { usuario ->
                        usuario.nombre.contains(texto, ignoreCase = true) ||
                                usuario.dni.contains(texto, ignoreCase = true)
                    }
                }
                adapterPagarCuota.actualizarLista(filtrada)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Cargar actividades desde DB
        val actividades = db.obtenerActividades()
        val adapterActividades = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, actividades)
        etActividades.setAdapter(adapterActividades)

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        // Redireccionar desde pagar cuota a comprobante
        btnRegistrarPago.setOnClickListener {
            val intent = Intent(this, ComprobanteActivity::class.java)
            startActivity(intent)
        }

        // Menú vertical de Forma de pago
        val adapterFormaDePago = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            formaDePago
        )

        etFormaDePago.setAdapter(adapterFormaDePago)
    }
}