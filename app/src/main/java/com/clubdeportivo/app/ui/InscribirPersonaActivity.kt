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

            val nombreCrudo = etNombre.text.toString().trim()
            val apellidoCrudo = etApellido.text.toString().trim()
            val dniCrudo = etDni.text.toString().trim()
            val telefonoCrudo = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val email = etEmail.text.toString().trim()

            // Capitalización: Primera letra en mayúscula para estandarizar la Base de Datos
            val nombre = nombreCrudo.split(" ").joinToString(" ") { palabra ->
                if (palabra.isNotEmpty()) palabra.replaceFirstChar { it.uppercase() } else ""
            }
            val apellido = apellidoCrudo.split(" ").joinToString(" ") { palabra ->
                if (palabra.isNotEmpty()) palabra.replaceFirstChar { it.uppercase() } else ""
            }

            // Expresiones regulares para nombres, dni y telefono
            val regexLetras = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$".toRegex()
            val regexDni = "^[0-9]{7,8}$".toRegex()
            val regexTelefono = "^[0-9]{8,15}$".toRegex()

            var valido = true

            // --- VALIDACIÓN NOMBRE ---
            if (nombre.isEmpty()){
                tilNombre.error = "El nombre es obligatorio"
                valido = false
            } else if (!regexLetras.matches(nombre)) {
                tilNombre.error = "Solo se permiten letras"
                valido = false
            } else if (nombre.split(" ").size > 3) {
                tilNombre.error = "Se permiten hasta 3 nombres"
                valido = false
            } else {
                tilNombre.error = null
            }

            // --- VALIDACIÓN APELLIDO ---
            if (apellido.isEmpty()){
                tilApellido.error = "El apellido es obligatorio"
                valido = false
            } else if (!regexLetras.matches(apellido)) {
                tilApellido.error = "Solo se permiten letras"
                valido = false
            } else if (apellido.split(" ").size > 3) {
                tilApellido.error = "Se permiten hasta 3 apellidos"
                valido = false
            } else {
                tilApellido.error = null
            }

            // --- VALIDACIÓN DNI ---
            if (dniCrudo.isEmpty()){
                tilDni.error = "El DNI es obligatorio"
                valido = false
            } else if (!regexDni.matches(dniCrudo)){
                tilDni.error = "Debe tener 7 u 8 números (sin puntos)"
                valido = false
            } else {
                if (db.existePersonaPorDni(dniCrudo)) {
                    tilDni.error = "Esta persona ya está registrada"
                    valido = false
                } else {
                    tilDni.error = null
                }
            }

            // --- VALIDACIÓN TELÉFONO ---
            if (telefonoCrudo.isEmpty()){
                tilTelefono.error = "El teléfono es obligatorio"
                valido = false
            } else if (!regexTelefono.matches(telefonoCrudo)){
                tilTelefono.error = "Ingresá un número válido (solo números, mín. 8 dígitos)"
                valido = false
            } else {
                tilTelefono.error = null
            }

            // --- VALIDACIÓN DIRECCIÓN ---
            if (direccion.isEmpty()){
                tilDireccion.error = "La dirección es obligatoria"
                valido = false
            } else {
                tilDireccion.error = null
            }

            // --- VALIDACIÓN EMAIL ---
            if (email.isEmpty()){
                tilEmail.error = "El e-mail es obligatorio"
                valido = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                tilEmail.error = "Ingresá un e-mail válido"
                valido = false
            } else {
                tilEmail.error = null
            }

            // --- VALIDACIÓN APTO MÉDICO ---
            if (!cbAptoMedico.isChecked){
                cbAptoMedico.error = "Debés tener el apto médico"
                valido = false
            } else {
                cbAptoMedico.error = null
            }

            if (!valido) return@setOnClickListener

            // Enviamos los datos LIMPIOS y CAPITALIZADOS a la siguiente pantalla
            val intent = Intent(this, CompletarInscripcionActivity::class.java).apply{
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
                putExtra("dni", dniCrudo)
                putExtra("telefono", telefonoCrudo)
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