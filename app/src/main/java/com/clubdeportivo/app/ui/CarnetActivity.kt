package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R

class CarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        val btnFinalizar = findViewById<Button>(R.id.btn_finalizar)

        // Datos recibidos
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val idSocio = intent.getStringExtra("idSocio") ?: ""
        val vencimiento = intent.getStringExtra("vencimiento") ?: ""


        // TextView / Layouts a cambiar
        val tvNombreApellido = findViewById<TextView>(R.id.tv_nombre_apellido)
        val tvDni = findViewById<TextView>(R.id.tv_dni)
        val tvId = findViewById<TextView>(R.id.tv_id)
        val tvVencimiento = findViewById<TextView>(R.id.tv_vencimiento)

        // Reemplazamos los datos
        tvNombreApellido.text = "${apellido}, ${nombre}"
        tvDni.text = dni
        tvId.text = idSocio.padStart(3, '0') // asi se ve 001, 002, etc
        tvVencimiento.text = vencimiento


        // Vuelve al Menú
        btnFinalizar.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}