package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.clubdeportivo.app.R
import com.clubdeportivo.app.db.DBClub
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InscribirPersonaActivity : AppCompatActivity() {
    private lateinit var db: DBClub
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_inscribir_persona)

        db = DBClub(this)

        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)
        val btnContinuar = findViewById<Button>(R.id.btn_completar_inscripcion)
        val cbAptoMedico = findViewById<android.widget.CheckBox>(R.id.cb_aptoMedico)

        val tilNombre = findViewById<TextInputLayout>(R.id.til_nombre)
        val tilApellido = findViewById<TextInputLayout>(R.id.til_apellido)
        val tilDni = findViewById<TextInputLayout>(R.id.til_dni)
        val tilTelefono = findViewById<TextInputLayout>(R.id.til_telefono)
        val tilDireccion = findViewById<TextInputLayout>(R.id.til_direccion)
        val tilEmail = findViewById<TextInputLayout>(R.id.til_email)

        val etNombre = findViewById<TextInputEditText>(R.id.et_nombre)
        val etApellido = findViewById<TextInputEditText>(R.id.et_apellido)
        val etDni = findViewById<TextInputEditText>(R.id.et_dni)
        val etTelefono = findViewById<TextInputEditText>(R.id.et_telefono)
        val etDireccion = findViewById<TextInputEditText>(R.id.et_direccion)
        val etEmail = findViewById<TextInputEditText>(R.id.et_email)

        // Cierra la pantalla
        flechaVolver.setOnClickListener {
            finish()
        }

        btnContinuar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val email = etEmail.text.toString().trim()

            var valido = true

            if (nombre.isEmpty()){
                tilNombre.error = "El nombre es obligatorio"
                valido = false
            } else{
                tilNombre.error = null
            }

            if (apellido.isEmpty()){
                tilApellido.error = "El apellido es obligatorio"
                valido = false
            } else{
                tilApellido.error = null
            }

            if (dni.isEmpty()){
                tilDni.error = "El DNI es obligatorio"
                valido = false
            } else if (dni.length!=8){
                tilDni.error = "El DNI debe tener 8 dígitos"
                valido = false
            } else{
                if (db.existePersonaPorDni(dni)) {
                    Toast.makeText(this, "Persona ya registrada.", Toast.LENGTH_SHORT).show()
                    valido = false
                }
                tilDni.error = null
            }

            if (telefono.isEmpty()){
                tilTelefono.error = "El teléfono es obligatorio"
                valido = false
            } else if (telefono.length <8){
                tilTelefono.error = "Ingresá un número válido (mínimo 8 dígitos)"
                valido = false
            } else{
                tilTelefono.error = null
            }

            if (direccion.isEmpty()){
                tilDireccion.error = "La dirección es obligatorio"
                valido = false
            } else{
                tilDireccion.error = null
            }

            if (email.isEmpty()){
                tilEmail.error = "El e-mail es obligatorio"
                valido = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                tilEmail.error = "Ingresá un e-mail válido"
                valido = false
            } else{
                tilEmail.error = null
            }

            if (!cbAptoMedico.isChecked){
                cbAptoMedico.error = "Debés tener el apto médico"
                valido = false
            } else{
                cbAptoMedico.error = null
            }

            if (!valido) return@setOnClickListener

            val intent = Intent(this, CompletarInscripcionActivity::class.java).apply{
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
                putExtra("dni", dni)
                putExtra("telefono", telefono)
                putExtra("direccion", direccion)
                putExtra("email", email)
            }
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        db.close() // cerrar la BD
    }
}