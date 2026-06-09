package com.clubdeportivo.app.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.adapters.PagarCuotaAdapter
import com.clubdeportivo.app.db.DBClub
import com.clubdeportivo.app.enums.FormaDePago
import com.clubdeportivo.app.models.UsuarioActivo
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class PagarCuotaActivity : AppCompatActivity() {
    private lateinit var db: DBClub
    private lateinit var adapterPagarCuota: PagarCuotaAdapter
    private val listaUsuariosCompleta = mutableListOf<UsuarioActivo>()
    private lateinit var fecha: String
    private var tipoSocio: String = ""
    private var idMiembro: Int? = null // USAR ESTE ID PARA GUARDAR LAS CUOTAS!!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        db = DBClub(this)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnRegistrarPago = findViewById<Button>(R.id.btn_registrar_pago)
        val tilActividades = findViewById<TextInputLayout>(R.id.til_actividades)
        val etActividades = findViewById<MaterialAutoCompleteTextView>(R.id.et_actividades)
        val etFormaDePago = findViewById<MaterialAutoCompleteTextView>(R.id.et_forma_de_pago)
        val etFecha = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_fechaDePago)
        // Searchbar Usuarios Activos
        val searchBar = findViewById<SearchBar>(R.id.sb_usuarios)
        val searchView = findViewById<SearchView>(R.id.sv_usuarios)
        val recycler = findViewById<RecyclerView>(R.id.rv_usuarios)
        // Enum forma de pago
        val formaDePago = FormaDePago.entries.map { it.texto }


        searchView.setupWithSearchBar(searchBar)
        // PEDIDO A BASE DE DATOS DE TODOS LOS MIEMBROS + dni + tipo de miembro
        val listaMiembros = db.obtenerTodosLosMiembros()
        listaUsuariosCompleta.clear()
        listaUsuariosCompleta.addAll(listaMiembros)

        if (listaMiembros.isEmpty()) {
            Toast.makeText(this, "No hay miembros registrados", Toast.LENGTH_SHORT).show()
        }

        // adaptador searchbar
        adapterPagarCuota = PagarCuotaAdapter(listaMiembros.toMutableList()) { seleccionado ->
            // Mostrar nombre, DNI y tipo en la SearchBar
            searchBar.setText("${seleccionado.nombre}")
            // GUARDO EL ID DEL USUARIO SELECCIONADO PARA LAS CONSULTAS A LA BD
            idMiembro = seleccionado.idSocio ?: seleccionado.idNoSocio
            // guardo tipo de socio para esconder/mostrar actividades
            tipoSocio = seleccionado.tipo
            // MOSTRAR / ESCONDER ACTIVIDADES
            if (seleccionado.idSocio != null) {
                tilActividades.visibility = View.GONE
            } else {
                tilActividades.visibility = View.VISIBLE
            }

            searchView.hide()
        }

        // Configurar SearchView
        searchView.setupWithSearchBar(searchBar)

        recycler.adapter = adapterPagarCuota
        recycler.layoutManager = LinearLayoutManager(this)

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
                                usuario.dni.contains(texto, ignoreCase = true) ||
                                usuario.tipo.contains(texto, ignoreCase = true)
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


        // DatePicker
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    fecha = "$dayOfMonth/${month + 1}/$year"
                    etFecha.setText(fecha)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

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