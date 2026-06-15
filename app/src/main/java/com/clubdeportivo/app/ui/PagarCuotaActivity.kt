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
import java.text.SimpleDateFormat
import java.util.Locale

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
        val etMonto = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_monto)
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

        // Registrar pago: valida los datos, guarda la cuota en la BD y va al comprobante
        btnRegistrarPago.setOnClickListener {
            registrarPago(
                etMonto = etMonto,
                etFormaDePago = etFormaDePago,
                etActividades = etActividades
            )

        }

        // Menú vertical de Forma de pago
        val adapterFormaDePago = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            formaDePago
        )

        etFormaDePago.setAdapter(adapterFormaDePago)
    }


    // Valida los datos del formulario, guarda el pago en la BD (cuota mensual o diaria)
    // y redirige al comprobante con los datos correspondientes
    private fun registrarPago(
        etMonto: com.google.android.material.textfield.TextInputEditText,
        etFormaDePago: MaterialAutoCompleteTextView,
        etActividades: MaterialAutoCompleteTextView
    ) {
        // Validaciones generales
        val idMiembroSeleccionado = idMiembro
        if (idMiembroSeleccionado == null) {
            Toast.makeText(this, "Selecciona un socio o no socio", Toast.LENGTH_SHORT).show()
            return
        }

        if (!::fecha.isInitialized || fecha.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha de pago", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            Toast.makeText(this, "Ingresa un monto válido (ej: 1500.50)", Toast.LENGTH_SHORT).show()
            return
        }

        val formaPagoTexto = etFormaDePago.text.toString()
        if (formaPagoTexto.isEmpty()) {
            Toast.makeText(this, "Selecciona una forma de pago", Toast.LENGTH_SHORT).show()
            return
        }

        // Datos de la persona (nombre, apellido y dni) para el comprobante
        val datosPersona = db.obtenerDatosPersona(idMiembroSeleccionado, tipoSocio == "Socio")
        if (datosPersona == null) {
            Toast.makeText(this, "No se encontraron los datos del miembro", Toast.LENGTH_SHORT).show()
            return
        }
        val (nombre, apellido, dni) = datosPersona

        if (tipoSocio == "Socio") {
            val idSocio = idMiembroSeleccionado.toLong()
            val vencimientoActual = db.obtenerVencimientoActualSocio(idSocio)
            if (vencimientoActual != null){
                val formato = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                val fechaPagoDate = formato.parse(fecha)
                val fechaVencDate = formato.parse(vencimientoActual)
                if (fechaPagoDate != null && fechaVencDate != null && !fechaPagoDate.after(fechaVencDate)){
                    Toast.makeText(
                        this,
                        "Cuota vigente hasta $vencimientoActual. La fecha de pago debe ser posterior.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }
            val nuevoVencimiento = calcularFechaVencimiento(fecha)

            val idCuota = db.insertarCuotaMensual(
                idSocio = idSocio,
                monto = monto,
                fechaPago = fecha,
                fechaVencimiento = nuevoVencimiento,
                formaPago = formaPagoTexto
            )
            if (idCuota == -1L) {
                Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
                return
            }
            // Actualiza el vencimiento del socio con el nuevo período pagado
            db.actualizarVencimientoSocio(idSocio, nuevoVencimiento)

            val intent = Intent(this, ComprobanteActivity::class.java).apply {
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
                putExtra("dni", dni)
                putExtra("idSocio", idSocio.toString())
                putExtra("vencimiento", nuevoVencimiento)
                putExtra("formaDePago", formaPagoTexto)
                putExtra("monto", monto)
            }
            startActivity(intent)
            finish()
        } else {
            // No Socio: requiere actividad seleccionada
            val actividad = etActividades.text.toString()
            if (actividad.isEmpty()) {
                Toast.makeText(this, "Selecciona una actividad", Toast.LENGTH_SHORT).show()
                return
            }
            val idActividad = db.obtenerIdActividadPorNombre(actividad)
            if (idActividad == null) {
                Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show()
                return
            }

            val idNoSocio = idMiembroSeleccionado.toLong()
            val idCuota = db.insertarCuotaDiaria(
                idNoSocio = idNoSocio,
                idActividad = idActividad,
                monto = monto,
                fechaPago = fecha,
                formaPago = formaPagoTexto
            )
            if (idCuota == -1L) {
                Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(this, ComprobanteActivity::class.java).apply {
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
                putExtra("dni", dni)
                putExtra("idNoSocio", idNoSocio.toString())
                putExtra("formaDePago", formaPagoTexto)
                putExtra("monto", monto)
                putExtra("actividad", actividad)
                putExtra("fecha", fecha)
            }
            startActivity(intent)
            finish()
        }
    }

    // Calcula la nueva fecha de vencimiento (1 mes después de la fecha de pago)
    private fun calcularFechaVencimiento(fechaPago: String): String {
        val partes = fechaPago.split("/")
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()
        val calendar = Calendar.getInstance().apply {
            set(anio, mes - 1, dia)
            add(Calendar.MONTH, 1)
        }
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    }
}