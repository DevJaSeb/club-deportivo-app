package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.clubdeportivo.app.db.DBClub

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var db: DBClub
    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRepetirPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        db = DBClub(this)

        etUsuario = findViewById(R.id.et_usuario)
        etPassword = findViewById(R.id.et_password)
        etRepetirPassword = findViewById(R.id.et_repetir_password)

        val btnRegistrar = findViewById<Button>(R.id.btn_registrar_usuario)
        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)

        btnRegistrar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val repetirPassword = etRepetirPassword.text.toString().trim()

            // Validación de campos vacíos
            if (usuario.isEmpty() || password.isEmpty() || repetirPassword.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de coincidencia de contraseñas
            if (password != repetirPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Intentar registrar en BD
            val exito = db.registrarUsuario(usuario, password)
            if (exito) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MenuActivity::class.java))
                finish() // cierra esta actividad para no volver atrás
            } else {
                Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }

        flechaVolver.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close() // cerrar la BD
    }
}