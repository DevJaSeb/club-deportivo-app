package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Button
import android.widget.RadioGroup
import android.app.DatePickerDialog
import android.widget.EditText
import android.widget.Toast
import java.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.db.DBClub
import com.clubdeportivo.app.enums.FormaDePago
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout


class CompletarInscripcionActivity : AppCompatActivity() {
    private lateinit var db: DBClub
    private lateinit var fecha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completar_inscripcion)

        db = DBClub(this)

        val rgTipoSocio = findViewById<RadioGroup>(R.id.rg_tipo_socio)
        val tilActividades = findViewById<TextInputLayout>(R.id.til_actividades)
        val etActividades = findViewById<MaterialAutoCompleteTextView>(R.id.et_actividades)
        val etFormaDePago = findViewById<MaterialAutoCompleteTextView>(R.id.et_forma_de_pago)
        val etFecha = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_fechaDePago)
        val etMonto = findViewById<EditText>(R.id.et_monto)
        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnFinalizarInscripcion = findViewById<Button>(R.id.btn_finalizar_inscripcion)

        // Datos recibidos
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val telefono = intent.getStringExtra("telefono") ?: ""
        val direccion = intent.getStringExtra("direccion") ?: ""
        val email = intent.getStringExtra("email") ?: ""

        // Formas de pago desde enum
        val formaDePago = FormaDePago.entries.map { it.texto }

        // Cargar actividades desde DB
        val actividades = db.obtenerActividades()
        val adapterActividades = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, actividades)
        etActividades.setAdapter(adapterActividades)

        // Adapter para forma de pago
        val adapterFormaPago = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, formaDePago)
        etFormaDePago.setAdapter(adapterFormaPago)

        // Volver
        flechaVolver.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

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

        // Cambio entre socio / no socio
        rgTipoSocio.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdo_socio -> {
                    tilActividades.visibility = android.view.View.GONE
                    etActividades.text?.clear()
                }
                R.id.rdo_noSocio -> {
                    tilActividades.visibility = android.view.View.VISIBLE
                }
            }
        }

        // Botón finalizar
        btnFinalizarInscripcion.setOnClickListener {
            // Validaciones generales
            if (fecha.isEmpty()) {
                Toast.makeText(this, "Selecciona una fecha de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipoSeleccionado = rgTipoSocio.checkedRadioButtonId
            if (tipoSeleccionado == -1) {
                Toast.makeText(this, "Selecciona Socio o No Socio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formaPagoText = etFormaDePago.text.toString()
            if (formaPagoText.isEmpty()) {
                Toast.makeText(this, "Selecciona una forma de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Leer monto actual
            val montoText = etMonto.text.toString()
            val monto = montoText.toDoubleOrNull()
            if (monto == null) {
                Toast.makeText(this, "Ingresa un monto válido (ej: 1500.50)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insertar persona
            val idPersona = db.insertarPersona(
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                telefono = telefono,
                direccion = direccion,
                email = email,
                fichaMedica = true,
                fechaInscripcion = fecha
            )
            if (idPersona == -1L) {
                Toast.makeText(this, "Error al guardar la persona", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (tipoSeleccionado) {
                R.id.rdo_socio -> {
                    val fechaVencimiento = calcularFechaVencimiento(fecha)
                    val idSocio = db.insertarSocio(idPersona, fechaVencimiento)
                    if (idSocio != -1L) {
                        Toast.makeText(this, "Socio registrado", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al registrar Socio", Toast.LENGTH_SHORT).show()
                    }

                    // Insertar cuota Mensual
                    val idCuotaMensual = db.insertarCuotaMensual(
                        idSocio = idSocio,
                        monto = monto,
                        fechaPago = fecha,
                        fechaVencimiento = fechaVencimiento,
                        formaPago = formaPagoText
                    )
                    if (idCuotaMensual != -1L) {
                        Toast.makeText(this, "Socio registrado", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al registrar cuota mensual", Toast.LENGTH_SHORT).show()
                    }


                }
                R.id.rdo_noSocio -> {
                    // Validar actividad
                    val actividad = etActividades.text.toString()
                    if (actividad.isEmpty()) {
                        Toast.makeText(this, "Selecciona una actividad", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val idActividad = db.obtenerIdActividadPorNombre(actividad)
                    if (idActividad == null) {
                        Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Insertar No Socio
                    val idNoSocio = db.insertarNoSocio(idPersona)
                    if (idNoSocio == -1L) {
                        Toast.makeText(this, "Error al registrar No Socio", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Insertar cuota diaria
                    val idCuota = db.insertarCuotaDiaria(
                        idNoSocio = idNoSocio,
                        idActividad = idActividad,
                        monto = monto,
                        fechaPago = fecha,
                        formaPago = formaPagoText
                    )
                    if (idCuota != -1L) {
                        Toast.makeText(this, "No Socio registrado", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al registrar cuota diaria", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

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