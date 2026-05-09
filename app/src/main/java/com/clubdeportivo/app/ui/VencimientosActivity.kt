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
import com.clubdeportivo.app.models.Vencimiento
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class VencimientosActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val cbFiltroDia = findViewById<CheckBox>(R.id.cb_filtro_dia)
        val layoutVacio = findViewById<LinearLayout>(R.id.layout_vacio)
        val rvVencimientos = findViewById<RecyclerView>(R.id.rv_vencimientos)
        val etFechaConsulta = findViewById<TextInputEditText>(R.id.et_fecha_consulta)

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        // Configurar el RecyclerView con datos de prueba
        rvVencimientos.layoutManager = LinearLayoutManager(this)

        // TODO: Cambiar. Crea la lista artificial de vencimientos.
        val listaDePrueba = listOf(
            Vencimiento("Álvarez, Bruno", "ID: 064"),
            Vencimiento("Benítez, Carla", "ID: 034"),
            Vencimiento("Cabrera, Diego", "ID: 022")
        )

        val adapter = VencimientosAdapter(listaDePrueba)
        rvVencimientos.adapter = adapter

        // Estado inicial: Mostramos la lista y ocultamos el estado vacío
        rvVencimientos.visibility = View.VISIBLE
        layoutVacio.visibility = View.GONE

        // Lógica del CheckBox para simular la búsqueda vacía
        cbFiltroDia.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Si se marca, simulamos que no hay resultados para ese día específico
                rvVencimientos.visibility = View.GONE
                layoutVacio.visibility = View.VISIBLE
            } else {
                // Si se desmarca, volvemos a mostrar la lista general
                rvVencimientos.visibility = View.VISIBLE
                layoutVacio.visibility = View.GONE
            }
        }

        val fechaDeHoy = Calendar.getInstance().time
        val formatoVisual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        etFechaConsulta.setText(formatoVisual.format(fechaDeHoy))

        etFechaConsulta.setOnClickListener {
            // Creamos el constructor del calendario
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setTitleText("Seleccionar Fecha")

            // Construimos el calendario
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                // La fecha viene en milisegundos (Long). Hay que darle formato de texto (dd/MM/yyyy)
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                // MaterialDatePicker trabaja en UTC, así que forzamos la zona horaria
                // para evitar que por el cambio de horario devuelva un día antes.
                formato.timeZone = TimeZone.getTimeZone("UTC")

                val fechaFormateada = formato.format(Date(selection))

                etFechaConsulta.setText(fechaFormateada)
            }

            picker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }
    }
}