package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R

class PagarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar_cuota)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnRegistrarPago = findViewById<Button>(R.id.btn_registrar_pago)

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        btnRegistrarPago.setOnClickListener {
            val intent = Intent(this, ComprobanteActivity::class.java)
            startActivity(intent)
        }
    }
}