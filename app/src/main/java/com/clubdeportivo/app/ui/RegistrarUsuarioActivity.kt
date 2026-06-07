package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.db.DBClub

class RegistrarUsuarioActivity : AppCompatActivity() {

    private lateinit var db: DBClub
    private lateinit var etUsuario: EditText
    private lateinit var etPass: EditText
    private lateinit var etRepetirPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        db = DBClub(this)

        etUsuario = findViewById(R.id.et_usuario)
        etPass = findViewById(R.id.et_pass)
        etRepetirPass = findViewById(R.id.et_repetir_pass)

        val btnRegistrar = findViewById<Button>(R.id.btn_registrar_usuario)
        val flechaVolver = findViewById<ImageView>(R.id.btn_volver)

        flechaVolver.setOnClickListener {
            finish()
        }

        btnRegistrar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val pass = etPass.text.toString().trim()
            val repetirPass = etRepetirPass.text.toString().trim()

            // Validación de campos vacíos
            if (usuario.isEmpty() || pass.isEmpty() || repetirPass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de coincidencia de contraseñas
            if (pass != repetirPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Intentar registrar en BD
            val exito = db.registrarUsuario(usuario, pass)
            if (exito) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("USUARIO", usuario)
                startActivity(intent)
                finish() // cierra esta actividad para no volver atrás
            } else {
                Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close() // cerrar la BD
    }
}