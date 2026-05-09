package com.clubdeportivo.app.ui

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.google.android.material.textfield.TextInputLayout

class CompletarInscripcionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completar_inscripcion)

        val rgTipoSocio = findViewById<RadioGroup>(R.id.rg_tipo_socio)
        val tilActividades = findViewById<TextInputLayout>(R.id.lbl_actividades)

        rgTipoSocio.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            when (checkedId) {
                R.id.rdo_socio -> {
                    tilActividades.isEnabled = false

                    // Limpiar el texto si había algo seleccionado
                    findViewById<AutoCompleteTextView>(R.id.lbl_actividades).text.clear()
                }

                R.id.rdo_noSocio -> {
                    // Si NO es Socio, lo volvemos a activar
                    tilActividades.isEnabled = true
                }
            }
        }
    }
}