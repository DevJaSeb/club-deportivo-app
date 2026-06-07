package com.clubdeportivo.app.models

data class UsuarioActivo(
    val nombre: String,
    val dni: String,
    val tipo: String   // "Socio" o "No Socio"
)
