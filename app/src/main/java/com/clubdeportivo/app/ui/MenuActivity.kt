package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnVencimientos = findViewById<Button>(R.id.btn_vencimientos)
        val btnSalir = findViewById<Button>(R.id.btn_salir)
        val btnPagarCuota = findViewById<Button>(R.id.btn_pagar_cuota)
        val btnInscribirPersona = findViewById<Button>(R.id.btn_inscribir_persona)

        btnVencimientos.setOnClickListener {
            val intent = Intent(this, VencimientosActivity::class.java)
            startActivity(intent)
        }

        btnSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            //FLAGS PARA CERRAR SESION CUANDO HAYA VALIDACION DE DATOS
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnInscribirPersona.setOnClickListener {
            val intent = Intent(this, InscribirPersonaActivity::class.java)
            startActivity(intent)
        }

        btnPagarCuota.setOnClickListener {
            val intent = Intent(this, PagarCuotaActivity::class.java)
            startActivity(intent)
        }
    }
}