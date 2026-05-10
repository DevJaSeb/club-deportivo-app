package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class CompletarInscripcionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completar_inscripcion)

        val rgTipoSocio = findViewById<RadioGroup>(R.id.rg_tipo_socio)
        val tilActividades = findViewById<TextInputLayout>(R.id.til_actividades)
        val etActividades = findViewById<MaterialAutoCompleteTextView>(R.id.et_actividades)
        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnFinalizarInscripcion = findViewById<Button>(R.id.btn_finalizar_inscripcion)

        // Vuelve al Menú
        flechaVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Ir a Carnet
        btnFinalizarInscripcion.setOnClickListener {
            val intent = Intent(this, ComprobanteActivity::class.java)
            startActivity(intent)
        }

        rgTipoSocio.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            when (checkedId) {
                R.id.rdo_socio -> {
                    tilActividades.isEnabled = false
                    etActividades.isEnabled = false

                    // Limpiar el texto si había algo seleccionado
                    // findViewById<AutoCompleteTextView>(R.id.lbl_actividades).text.clear()
                    etActividades.text?.clear()
                }

                R.id.rdo_noSocio -> {
                    // Si NO es Socio, lo volvemos a activar
                    tilActividades.isEnabled = true
                    etActividades.isEnabled = true
                }
            }
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

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            actividades
        )

        etActividades.setAdapter(adapter)
    }
}