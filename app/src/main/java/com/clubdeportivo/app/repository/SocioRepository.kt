package com.clubdeportivo.app.repository

import android.content.Context
import com.clubdeportivo.app.db.DBClub
import com.clubdeportivo.app.models.Vencimiento

class SocioRepository(context: Context) {

    private val dbHelper = DBClub(context)

    fun obtenerVencimientos(fecha: String, filtrarPorDia: Boolean): List<Vencimiento> {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Vencimiento>()

        var sql = "SELECT s.idSocio, p.apellido, p.nombre FROM socio s INNER JOIN persona p ON s.idPersona = p.idPersona"
        var args: Array<String>? = null

        if (filtrarPorDia) {
            sql += " WHERE s.fechaVencimiento = ?"
            args = arrayOf(fecha)
        }

        val cursor = db.rawQuery(sql, args)

        if (cursor.moveToFirst()) {
            do {
                val idSocio = cursor.getInt(0)
                val apellido = cursor.getString(1)
                val nombre = cursor.getString(2)

                val nombreCompleto = "$apellido, $nombre"
                val idFormateado = "ID: ${idSocio.toString().padStart(3, '0')}"

                lista.add(Vencimiento(nombreCompleto, idFormateado))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return lista
    }
}