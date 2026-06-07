package com.clubdeportivo.app.ui

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clubdeportivo.app.R
import com.clubdeportivo.app.adapters.VencimientosAdapter
import com.clubdeportivo.app.db.DBClub
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class VencimientosActivity: AppCompatActivity() {

    private lateinit var db: DBClub
    private lateinit var rvVencimientos: RecyclerView
    private lateinit var layoutVacio: LinearLayout
    private lateinit var etFechaConsulta: TextInputEditText
    private lateinit var cbFiltroDia: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        db = DBClub(this)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        cbFiltroDia = findViewById(R.id.cb_filtro_dia)
        layoutVacio = findViewById(R.id.layout_vacio)
        rvVencimientos = findViewById(R.id.rv_vencimientos)
        etFechaConsulta = findViewById(R.id.et_fecha_consulta)

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        // Configurar el RecyclerView
        rvVencimientos.layoutManager = LinearLayoutManager(this)

        // Establecer fecha de hoy por defecto
        val fechaDeHoy = Calendar.getInstance().time
        val formatoVisual = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        etFechaConsulta.setText(formatoVisual.format(fechaDeHoy))

        actualizarLista()

        cbFiltroDia.setOnCheckedChangeListener { _, _ ->
            actualizarLista()
        }

        etFechaConsulta.setOnClickListener {
            // Creamos el constructor del calendario
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTitleText("Seleccionar Fecha")
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                // La fecha viene en milisegundos (Long). Hay que darle formato de texto (d/M/yyyy)
                val formato = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                // MaterialDatePicker trabaja en UTC, así que forzamos la zona horaria
                // para evitar que por el cambio de horario devuelva un día antes.
                formato.timeZone = TimeZone.getTimeZone("UTC")

                val fechaFormateada = formato.format(Date(selection))

                etFechaConsulta.setText(fechaFormateada)
                actualizarLista()
            }

            picker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }
    }

    private fun actualizarLista() {
        val fechaSeleccionada = etFechaConsulta.text.toString()
        val filtrarPorDia = cbFiltroDia.isChecked

        val resultados = db.obtenerVencimientos(fechaSeleccionada, filtrarPorDia)

        if (resultados.isEmpty()) {
            rvVencimientos.visibility = View.GONE
            layoutVacio.visibility = View.VISIBLE
        } else {
            rvVencimientos.adapter = VencimientosAdapter(resultados)
            rvVencimientos.visibility = View.VISIBLE
            layoutVacio.visibility = View.GONE
        }
    }
}