package com.clubdeportivo.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clubdeportivo.app.R
import com.clubdeportivo.app.db.DBClub
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DBClub(this)
        val db = dbHelper.readableDatabase

        val tilUsuario = findViewById<TextInputLayout>(R.id.lbl_usuario)
        val tilPass = findViewById<TextInputLayout>(R.id.lbl_pass)

        val etUsuario = findViewById<TextInputEditText>(R.id.et_usuario)
        val etPass = findViewById<TextInputEditText>(R.id.et_pass)

        val btnIniciarSesion = findViewById<Button>(R.id.btn_login)

        val btnRegistrarUsuario = findViewById<TextView>(R.id.btn_registrar_usuario)

        btnIniciarSesion.setOnClickListener {

            val usuario = etUsuario.text.toString().trim()
            val pass = etPass.text.toString().trim()

            var valido = true

            if (usuario.isEmpty()){
                tilUsuario.error = "Ingresá tu usuario"
                valido = false
            } else{
                tilUsuario.error = null
            }

            if (pass.isEmpty()){
                tilPass.error = "Ingresá tu contraseña"
                valido = false
            } else{
                tilPass.error = null
            }

            if (!valido) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: poner autenticaciones reales cuando agreguemos bases de datos.

            // Chequear credenciales con la base de datos
            val esValido = dbHelper.verificarLogin(usuario, pass)
            if (esValido) {
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                // Iniciar actividad principal después de login
                val intent = Intent(this, MenuActivity::class.java)
                intent.putExtra("USUARIO", usuario)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
        // Botón crear nuevo usuario
        btnRegistrarUsuario.setOnClickListener {
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }
    }
}