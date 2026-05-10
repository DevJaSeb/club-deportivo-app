package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R

class InscribirPersonaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscribir_persona)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnContinuar = findViewById<Button>(R.id.btn_completar_inscripcion)

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        btnContinuar.setOnClickListener {
            val intent = Intent(this, CompletarInscripcionActivity::class.java)
            startActivity(intent)
        }
    }
}